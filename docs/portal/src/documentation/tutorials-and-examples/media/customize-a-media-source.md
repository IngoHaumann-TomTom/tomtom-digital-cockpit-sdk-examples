---
title: Customize a Media Source
---

Most media apps offer content that can be browsed and played freely, leaving the user more or less
in control of the playback. Potentially, these apps require logging in before allowing access. This
kind of media app is easier to customize, as the TomTom IndiGO Application Suite already provides a
delightful user interface for media.

These customization capabilities are currently offered:
- Modify how content provided by the media app is displayed to the user.
  See the [content display](#modifying-content-display) section for details.
- Add new custom action icons specific to an app.
  See the [custom action](#add-a-custom-action) section for details.
- Select which app name, icon and colors are displayed to the user in different contexts.
  See the [name and icon](#customize-name-and-icon) section for details.
- Define how media items provided by the media app are compared with each other.
  See the documentation of type `MediaItemComparisonPolicy` in the package
  [`com.tomtom.ivi.appsuite.media.api.common.frontend.policies`](TTIVI_PLATFORM_API)
  for more information.

These customizations are possible through the use of policies, which are applied based on the
media source that is currently being browsed. After creating a new policy with any number of
customizations, the [policy installation](#install-customization-policies) section explains how to
apply them to the build.

More venues for customization are planned for the future, but if it is necessary to change the user
experience in a more radical way, then it is possible to
[create a new user interface](/tomtom-indigo/documentation/tutorials-and-examples/media/create-a-new-media-user-interface).

Throughout this customization guide, a practical example will be built to integrate TomTom IndiGO
with an internet radio media app. This fictitious app will be called `ExampleInternetRadio`.

For this guide, knowledge of the TomTom IndiGO media APIs
[`appsuite_media_api_common_core`](TTIVI_PLATFORM_API) and
[`appsuite_media_api_common_frontend`](TTIVI_PLATFORM_API) will be of great help. In the
[media overview](/tomtom-indigo/documentation/development/platform-domains/media)
documentation, more details can be found about the Android Automotive Media framework and how TomTom
IndiGO uses it.

If you want to configure the media plugins with a more global configuration, you can follow
this guide:
[How to configure the media plugins](/tomtom-indigo/documentation/tutorials-and-examples/media/configure-the-media-plugins).

## Modifying content display

The concepts presented here are implemented in applications that you can find in
`examples/media/custompolicies` and `examples/media/miniplayer`.

A media source might provide poor quality of its input data, due to a sub-par implementation of the
Android Automotive Media API, causing the default user interface to display it incorrectly.

When browsing through content from the `ExampleInternetRadio` app, normally the TomTom IndiGO user
interface would display the [`IviMediaItem`](TTIVI_PLATFORM_API)`.title` field of a playable media
item in a more visually-prominent fashion (in bold and with a bigger font size) and the
[`IviMediaItem`](TTIVI_PLATFORM_API)`.subtitle` field, when present, in a less-prominent fashion
(not in bold, and with a smaller font size).

When browsing stations from `ExampleInternetRadio`, a playable radio station media item has: its
[`IviMediaItem`](TTIVI_PLATFORM_API)`.title` field set to the name of the station; its
[`IviMediaItem`](TTIVI_PLATFORM_API)`.subtitle` field set to the full current track name,
"Artist - Track Name"; and its [`IviMediaItem`](TTIVI_PLATFORM_API)`.artist` field is left
empty.

With such data, the track name will always be shown smaller, and only the radio station's name will
always be at the center of the driver's attention.
The wish in this example is to invert the `title` and the `subtitle` so that the current track name
is displayed more prominently, and the radio station's name is shown in a less prominent way.

The first step to achieve this result is to create a new module made to contain all code related to
`ExampleInternetRadio`. In this new module, a new `MediaItemMappingPolicy` object (see
package [`com.tomtom.ivi.appsuite.media.api.common.frontend.policies`](TTIVI_PLATFORM_API)), should
be created:

[`src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourceItemMappingPolicy.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/media/custompolicies/src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourceItemMappingPolicy.kt#L24-L31)

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.appsuite.media.api.common.core.IviMediaItem
import com.tomtom.ivi.appsuite.media.api.common.core.IviMediaItemBuilder
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.MediaItemMappingPolicy

class ExampleInternetRadioMediaItemMappingPolicy : MediaItemMappingPolicy {
    override fun invoke(item: IviMediaItem): IviMediaItem {
        return IviMediaItemBuilder(item)
            .withTitle(item.subtitle ?: "")     // The subtitle is set to the title field.
            .withDisplaySubtitle(item.title)    // The title is set to the subtitle field.
            .build()
    }
}
```

A `MediaItemMappingPolicy` customizes how the user interface will display media item data, and can
be as complex as necessary: it might be useful to examine whether an item is playable or browsable
(or both) before making a decision on how to map the [`IviMediaItem`](TTIVI_PLATFORM_API) fields; or
maybe the [`IviMediaItem`](TTIVI_PLATFORM_API)`.id` should be parsed to find out what type of
content is being played. It all depends on how the media source presents its information. Also note
that this data might change over time, posing integration issues when new versions are released
potentially with changes in media item data provisioning.

The new `ExampleInternetRadioMediaItemMappingPolicy` data mapping policy class needs to be
specified in a [`PolicyProvider`](TTIVI_PLATFORM_API), which will only be used  when browsing
`ExampleInternetRadio` content.
The [policy installation](#install-customization-policies) section explains how.

## Add a custom action

Media sources in the Android Automotive Media framework are allowed to provide custom actions for
users to perform source-specific tasks, such as saving a track in the user's "Liked tracks"
playlist.

The `ExampleInternetRadio` media source contains a feature to save favorite radio stations to the
home page. When playing a radio station, the user should be able to press a button with a heart
icon, and while browsing, that station will always be shown on the home page for quick access. When
playing a station, it is always possible to remove it from the quick access list by pressing the
heart icon again.

During playback, a media source will constantly advertise which custom actions are currently
available for the user to perform. The [`MediaService`](TTIVI_PLATFORM_API) service API contains an
`availableActions` field, which is always updated with a list of whatever actions the
media source provides for the media that is currently being played,
`activeMediaItem`.

Such list of [`Action`](TTIVI_PLATFORM_API)s will have to be examined to find out what `Actions` are
available. When `ExampleInternetRadio` is playing a track, the device's logcat will show events from
the stock [`MediaService`](TTIVI_PLATFORM_API) called `onAvailableActionsChanged`, such as this one:

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

A new [`ActionMediaControl`](TTIVI_PLATFORM_API) is needed. Since this seems to be a simple toggle
behavior, the [`ToggleActionMediaControl`](TTIVI_PLATFORM_API) specialization fits the use case
better:

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.appsuite.media.api.common.core.actions.Action
import com.tomtom.ivi.appsuite.media.api.common.frontend.controls.ToggleActionMediaControl

class HeartActionMediaControl(context: MediaControlContext) :
    ToggleActionMediaControl(context) {

    override val drawable: LiveData<DrawableResolver> =
        MutableLiveData(ResourceDrawableResolver(R.drawable.example_internet_radio_heart_icon))

    override val activateAction = Action("heart_this_station")

    override val activatedAction = Action("un-heart_this_station")

    object Factory : MediaControlFactory {
        override fun createControlFor(mediaControlContext: MediaControlContext) =
            HeartActionMediaControl(mediaControlContext)
    }
}
```

Note that in this example the new media control is a _custom_ media control. All media controls
which use [`StandardActionId`](TTIVI_PLATFORM_API) IDs are considered _standard_, all others are
considered _custom_ media controls. The documentation for [`MediaControlPolicy`](TTIVI_PLATFORM_API)
offers more information about how to work with standard media controls.

The new `HeartActionMediaControl` should then be specified in a
[`MediaControlPolicy`](TTIVI_PLATFORM_API).

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.MediaControlPolicy

// Note that this class also exposes `replacedStandardControls`, an `override`-able map of
// `StandardMediaControls` used to replace standard media controls such as play/pause.
class ExampleInternetRadioMediaControlPolicy : MediaControlPolicy {

    override val customControls = listOf(HeartActionMediaControl.Factory)
}
```

Finally, the new `ExampleInternetRadioMediaControlPolicy` needs to be specified in a
[`PolicyProvider`](TTIVI_PLATFORM_API) which will only be used  when browsing `ExampleInternetRadio`
content. The [policy installation](#install-customization-policies) section explains how.

## Customize name and icon

Branding is a very important aspect of customization. The media app's logo usually needs to be
presented in a way consistent with branding guidelines, available space for the logo, and the current
theme's colors.

The company behind the `ExampleInternetRadio` app may have the following branding guidelines:
The name must be fully displayed as "Example Internet Radio", if possible, and only if not possible
(for example, due to insufficient space in the UI), the name must then be displayed as
"ExampleRadio". Different icons and colors are also prescribed for light and dark themes.

To aid the process of adherence to these branding guidelines, the
[`MediaSourceAttributionPolicy`](TTIVI_PLATFORM_API) policy is available. A
[`SourceAttributionFormat`](TTIVI_PLATFORM_API) parameter can be used to specify the preferred
UI display mode depending on the current display context. The `mediaItem` parameter can be used to
determine what to display based on the metadata contained in the current media item.

[`src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourceSourceAttributionPolicy.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/media/custompolicies/src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourceSourceAttributionPolicy.kt#L28-L49)

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.appsuite.media.api.common.core.IviMediaItem
import com.tomtom.ivi.appsuite.media.api.common.core.SourceInfo
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.MediaSourceAttributionPolicy
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.SourceAttributionFormat
import com.tomtom.tools.android.api.resourceresolution.drawable.DrawableResolver
import com.tomtom.tools.android.api.resourceresolution.drawable.ResourceDrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.ResourceStringResolver
import com.tomtom.tools.android.api.resourceresolution.string.StringResolver

class ExampleInternetRadioSourceAttributionPolicy : MediaSourceAttributionPolicy {
    override fun getName(
        sourceInfo: SourceInfo?,
        mediaItem: IviMediaItem?,
        sourceAttributionFormat: SourceAttributionFormat
    ): StringResolver? {
        return if (sourceAttributionFormat.preferSimplified)
            ResourceStringResolver(R.string.simple_name)
        else
            ResourceStringResolver(R.string.full_name)
    }

    override fun getLogo(
        sourceInfo: SourceInfo?,
        mediaItem: IviMediaItem?,
        sourceAttributionFormat: SourceAttributionFormat
    ): DrawableResolver {
        return if (sourceAttributionFormat.preferWordMark)
            ResourceDrawableResolver(R.drawable.ic_sel_logo_with_word_mark)
        else
            ResourceDrawableResolver(R.drawable.ic_sel_logo_only)
    }

    override fun getStyle(): SourceStyle {
        return SourceStyle(AttrColorResolver(R.attr.exampleinternetradio_accent_color))
    }
}
```

The `getName()` method uses the right string at the right time, and the `getLogo()` method uses
resources which are selected by Android by the type of theme currently in use.
The `getStyle()` method can return a [`SourceStyle`](TTIVI_PLATFORM_API) to override the default
`accentColor` provided by the source APK theme. When not specified, it returns `null` and will keep
the default color.

The new `ExampleInternetRadioSourceAttributionPolicy` needs to be specified in a
[`PolicyProvider`](TTIVI_PLATFORM_API) which will be used only when browsing `ExampleInternetRadio`
content. Section [policy installation](#install-customization-policies) explains how to do this.

## Install customization policies

Policy configuration is done using a [`MediaPolicyFrontendExtension`](TTIVI_PLATFORM_API). When the
user starts browsing the internet radio, this configuration will be used to customize the user
experience:

[`src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourceFrontendExtension.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/media/custompolicies/src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourceFrontendExtension.kt#L28-L32)

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaPolicyFrontendExtension
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendExtension

val exampleInternetRadioFrontendExtension: FrontendExtension =
    MediaPolicyFrontendExtension(
        // This needs to match the Android package name of the media app.
        sourcePackageName = "com.example.exampleinternetradio",
        // The class name is optional and only required if the policy must explicitly target a
        // specific MediaBrowserService in the package, such as when the app contains more than one
        // such service. This is needed to apply a different policy per service within the same
        // package.
        sourceClassName = "com.example.MediaService",
        policyProvider = exampleInternetRadioPolicyProvider
    )
```

This is an example defining how `exampleInternetRadioPolicyProvider` might look with all the
customizations described in the other sections.

[`src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourcePolicyProvider.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/media/custompolicies/src/main/kotlin/com/example/ivi/example/media/custompolicies/ExampleMediaSourcePolicyProvider.kt#L24-L30)

```kotlin
package com.example.exampleinternetradio

import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.PolicyProvider

val exampleInternetRadioPolicyProvider = PolicyProvider(
    itemMappingPolicy = ExampleInternetRadioMediaItemMappingPolicy(),
    sourceAttributionPolicy = ExampleInternetRadioSourceAttributionPolicy(),
    mediaControlPolicy = ExampleInternetRadioMediaControlPolicy()
)
```

Activating the new `exampleInternetRadioFrontendExtension` is done via a Gradle configuration.
The guide on
[How to create a frontend plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
and the documentation for [`FrontendExtension`](TTIVI_PLATFORM_API) are available for more details.
