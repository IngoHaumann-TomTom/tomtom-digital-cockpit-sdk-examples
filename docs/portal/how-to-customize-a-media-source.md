# How to customize a media source

## Introduction

Most media apps offer content which can be browsed and played liberally, letting the user more or
less in control of the playback. Potentially, these apps require logging in before allowing access.
This kind of media apps is easier to customize, as the IndiGO Application Suite already provides a
delightful user interface for media.

These customization capabilities are currently offered:
- Modify how content provided by the media app is displayed to the user.
  See the [content display](#modify-content-display) section for details.
- Add new custom action icons specific to an app.
  See the [custom action](#add-a-custom-action) section for details.

These customizations are possible through the use of policies, which are applied based on the
media source that is currently being browsed.

More venues for customization are planned for the future, but if it is necessary to change the user
experience in a more radical fashion, then it is possible to [create a new user interface](how-to-customize-a-media-source.md).

For this guide, knowledge of the IndiGO media APIs [api_common_media] and [api_common_mediasources]
will be of great help. In the [media overview](media-domain.md) documentation, more details can be
found over the Android Automotive Media framework and how IndiGO uses it.

## Modify content display

A media source might provide poor quality of its input data, due to a sub-par implementation of the
Android Automotive Media API, causing the default user interface to display it incorrectly.

A practical example: an internet radio media source. We will call it `ExampleInternetRadio`.
Normally the IndiGO user interface would display the [title](com.tomtom.ivi.api.common.media.titleOrNull)
field of a playable media item in a visually more prominent fashion (in bold and with a bigger font
size) and the [subtitle](com.tomtom.ivi.api.common.media.subtitleOrNull) field, when present, in a
less prominent fashion (not in bold, and with a smaller font size).

When browsing stations from `ExampleInternetRadio`, a playable radio station media item has: its
[title](com.tomtom.ivi.api.common.media.titleOrNull) field set to the name of the station; its
[subtitle](com.tomtom.ivi.api.common.media.subtitleOrNull) field set to the full current track name,
"Artist - Track Name"; and its [artist](com.tomtom.ivi.api.common.media.artistOrNull) field is left
empty.

With such data, the track name will always be shown smaller, and only the radio station's name will
always be at the center of the driver's attention. 
The wish in this example is to invert the `title` and the `subtitle` so that the current track name
is displayed more prominently, and the radio station's name is shown in a less prominent way.

The first step to achieve this is to create a new module made to contain all code related to
`ExampleInternetRadio`.
In this new module, a new [MediaItemDataExtractionPolicy](com.tomtom.ivi.api.common.mediasources.policies.MediaItemDataExtractionPolicy)
should be created:

```kotlin
package com.example.exampleinternetradio

class ExampleInternetRadioMediaItemDataExtractionPolicy : MediaItemDataExtractionPolicy {
    override fun invoke(item: MediaItem): MediaItemData {
        return MediaItemData(
            title = item.subtitleOrNull() ?: "",  // The subtitle is set to the title field.
            subtitle = item.titleOrNull(),        // The title is set to the subtitle field.
            artworkUri = item.mediaArtUriOrNull(),
            isPlayable = item.isPlayable(),
            isBrowsable = item.isBrowsable(),
            durationMs = item.durationMs()
        )
    }
}
```

[MediaItemDataExtractionPolicy](com.tomtom.ivi.api.common.mediasources.policies.MediaItemDataExtractionPolicy)
is the main way to customize how the user interface will display media item data, and can be as
complex as necessary: it might be useful to examine whether an item is playable or browsable (or
both) before making a decision on how to fill the [MediaItemData](com.tomtom.ivi.api.common.mediasources.MediaItemData)
fields; or maybe the [Media ID](com.tomtom.ivi.api.common.media.mediaId) should be parsed to find
out what type of content is being played. It all depends on how the media source presents its
information. Also note that this data might change over time, posing integration issues when new
versions are released potentially with changes in media item data provisioning.

The new `ExampleInternetRadioMediaItemDataExtractionPolicy` data extraction policy class now needs
to be specified in a new [PolicyProvider](com.tomtom.ivi.api.common.mediasources.policies.PolicyProvider)
which will be used only when browsing `ExampleInternetRadio` content.

To apply this policy provider only when browsing content from `ExampleInternetRadio`, a change to
the default [MediaConfiguration](com.tomtom.ivi.api.common.mediasources.MediaConfiguration) is
necessary during app initialization, or anyway before starting to browse media content:

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.api.common.mediasources.policies.PolicyProvider
import com.tomtom.ivi.api.defaults.mediasources.defaultMediaConfiguration

// This needs to match the Android MediaBrowserService package and class name of the media source.
object ExampleInternetRadioSourceId :
    SourceId("com.example.exampleinternetradio", "com.example.MediaService")

// ...
init {
    defaultMediaConfiguration.overridePolicyProvider(
        ExampleInternetRadioSourceId,
        PolicyProvider(extractItemData = ExampleInternetRadioMediaItemDataExtractionPolicy())
    )
}
```

That's it. The default configuration offers more customization options, check the
[defaultMediaConfiguration](com.tomtom.ivi.api.defaults.mediasources.defaultMediaConfiguration) docs
for more information.

## Add a custom action

Media sources in the Android Automotive Media framework are allowed to provide custom actions for
users to perform source-specific tasks, such as saving a track in the user's "Liked tracks"
playlist.

A practical example: an internet radio media source. We will call it `ExampleInternetRadio`.
This media source has the feature to save favorite radio stations in the home page. When playing a
radio station, the user should be able to press a button with an heart icon, and while browsing,
that station will be always shown in the home page for quick access. When playing a station which is
not that good anymore, it is always possible to remove it from quick access by pressing the heart
icon again.

During playback, a media source will constantly advertise which custom actions are currently
available for the user to perform. The [MediaService](com.tomtom.ivi.core.serviceapi.media.MediaService)
service API contains an [availableActions](com.tomtom.ivi.core.serviceapi.media.MediaService.availableActions)
field, always updated with a list of whatever actions the media source provides for the media that
is currently being played, [activeMediaItem](com.tomtom.ivi.core.serviceapi.media.MediaService.activeMediaItem).

Such list of [Action](com.tomtom.ivi.api.common.media.actions.Action)s will have to be examined to
find out what are available.
When `ExampleInternetRadio` is playing a track, the device's logcat will show events from the stock
[MediaService](com.tomtom.ivi.core.serviceapi.media.MediaService) called `onAvailableActionsChanged`,
such as this one:

```
MediaServiceBase: event=onAvailableActionsChanged([PauseAction(id=ivi_media:pause), SkipMediaItemForwardAction(id=ivi_media:skip_media_item_forward), Action(id=heart_this_station)])
```

When playing a radio station which was already saved by pressing the heart button, the
`onAvailableActionsChanged` event will look something like this:

```
MediaServiceBase: event=onAvailableActionsChanged([PauseAction(id=ivi_media:pause), SkipMediaItemForwardAction(id=ivi_media:skip_media_item_forward), Action(id=un-heart_this_station)])
```

The ID `un-heart_this_station` from the last `Action(id=un-heart_this_station)` is then needed to
remove the favorite state from the current station.

A new [ActionMediaControl](com.tomtom.ivi.api.common.mediasources.controls.ActionMediaControl) is needed.
Since this seems to be a simple toggle behavior, the [ToggleActionMediaControl](com.tomtom.ivi.api.common.mediasources.controls.ToggleActionMediaControl)
specialization fits better the use case:

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.api.common.mediasources.controls.ToggleActionMediaControl

// This needs to match the Android MediaBrowserService package and class name of the media source.
object ExampleInternetRadioSourceId :
    SourceId("com.example.exampleinternetradio", "com.example.MediaService")

class HeartActionMediaControl(context: MediaControlContext) :
    ToggleActionMediaControl(context, ExampleInternetRadioSourceId) {

    override val id = "example_media_source_heart_action"

    override val drawable: LiveData<DrawableResolver> =
        MutableLiveData(
            ResourceDrawableResolver(R.drawable.example_internet_radio_heart_icon)
        )

    override val activateActionId
        get() = "heart_this_station"

    override val activatedActionIds
        get() = arrayOf("un-heart_this_station")

    object Factory : MediaControlFactory {
        override fun createControlFor(mediaControlContext: MediaControlContext) =
            HeartActionMediaControl(mediaControlContext)
    }
}
```

To add this new control only when browsing content from `ExampleInternetRadio`, a change to the default
[MediaConfiguration](com.tomtom.ivi.api.common.mediasources.MediaConfiguration) is necessary during
app initialization, or anyway before starting to browse media content:

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.api.common.mediasources.policies.PolicyProvider
import com.tomtom.ivi.api.defaults.mediasources.defaultMediaConfiguration

// ...
init {
    defaultMediaConfiguration.appendSecondaryMediaControl(HeartActionMediaControl.Factory)
}
```

Note that in this example the action is added as a _secondary_ media control: refer to the
[MediaConfiguration](com.tomtom.ivi.api.common.mediasources.MediaConfiguration) documentation for
more information on the difference between _primary_ and _secondary_ media controls.

That's it. The default configuration offers more customization options, check the
[defaultMediaConfiguration](com.tomtom.ivi.api.defaults.mediasources.defaultMediaConfiguration) docs
for more information.
