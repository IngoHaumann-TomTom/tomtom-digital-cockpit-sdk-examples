# How to create a new media user interface

## Introduction

The IndiGO Application Suite provides a default user interface (UI). This user interface might not
be suitable for a media app (also called "media source"), when it does not conform to the typical
organization of content, or when their offered content can not be liberally browsed or played or
controlled.
Such sources would look disappointing or completely unusable in the default user interface from the
IndiGO Application Suite.

A few examples of media apps which are not suitable for the default user interface:
- A controller/player for an AM/FM/DAB+ radio tuner installed in the system:
  the default user interface would show the bands, stations, and favorites lumped together as tabs,
  and dump together all known stations in a single tab.
- An app which can only stream music (for example via Bluetooth, or via Wi-Fi):
  No content would be available for browsing, without explanation.
- An app that only displays remotely broadcast video streams:
  The default user interface is not suitable for video playback, and if the app would only receive
  broadcast video streams, no content would be available for browsing, with no explanation.

If such drastic customizations are not necessary, for example if the only needs are fixing up how
the content from a media app is displayed to the user, or to add an icon to perform an action
specific to that app, using the default media user interface should be preferred and
[appropriately customized](how-to-customize-a-media-source.md).

For this guide, knowledge of the IndiGO media APIs [api_common_media] and [api_common_mediasources]
will greatly help. In the [media overview](/indigo/media-domain.md) documentation, more details can
be found over the Android Automotive Media framework and how IndiGO uses it. 

## Concepts

The guide will implement the user interface for a simple radio made available in the system through
the standard [Android Automotive Radio API](https://source.android.com/devices/automotive/radio).
Through the standard API for media, an Android [media browser service](https://developer.android.com/reference/androidx/media/MediaBrowserServiceCompat)
will provide access to an hardware radio tuner module.

This user interface will be a new panel. A panel is composed by three classes: a [TaskPanel](com.tomtom.ivi.api.framework.frontend.panels.TaskPanel)
to define the logic; a view model, [FrontendViewModel](com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel),
potentially using data binding; an [IviFragment](com.tomtom.ivi.api.framework.frontend.IviFragment)
defining the Android fragment and creating the instance of the view model.
Please refer to the [frontend plugin guide](how-to-create-frontend-plugin.md) to get started.

This user interface will use a specialization of the base user interface panel type [TaskPanel](com.tomtom.ivi.api.framework.frontend.panels.TaskPanel),
[MediaTaskPanel](com.tomtom.ivi.api.common.mediasources.MediaTaskPanel). This panel type is
more suitable for media apps, as it contains media-specific facilities.

This being simply an example, the user interface is very sparse and only contains one panel to
display, and no functionality other than basic browsing and playing.

### Example panel

The example's panel class, based on [MediaTaskPanel](com.tomtom.ivi.api.common.mediasources.MediaTaskPanel),
enables browsing through both the stations recognized by the radio, and the raw frequencies offered
by each radio band.

In the panel the media [Options](com.tomtom.ivi.api.common.media.Options) are used to print out to
the device logcat all media items retrieved by all media services: this helps _only_ in the initial
discovery phase to analyze the format of all content returned by the media source. This setting
*must not* be used in production, and will not function in _release_ builds.

The [RootSourceClient](com.tomtom.ivi.api.common.media.RootSourceClient) is used to browse the
content and retrieve the available categories (in the case of radio, those are bands and stations),
while the [MediaService](com.tomtom.ivi.core.serviceapi.media.MediaService) lets the user play a
radio.

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.media2.common.MediaItem
import com.tomtom.ivi.api.common.media.Options
import com.tomtom.ivi.api.common.media.RootSourceClient
import com.tomtom.ivi.api.common.media.SourceId
import com.tomtom.ivi.api.common.media.actions.standard.PlayMediaIdFromSourceAction
import com.tomtom.ivi.api.common.mediasources.MediaTaskPanel
import com.tomtom.ivi.api.common.mediasources.MediaFrontendContext
import com.tomtom.ivi.api.framework.frontend.IviFragment
import com.tomtom.ivi.core.serviceapi.media.MediaService
import com.tomtom.ivi.core.serviceapi.media.createApi

// This is an example source ID, matching the default Android Automotive car radio service.
object RadioSourceId :
    SourceId("com.android.car.radio", "com.android.car.radio.service.RadioAppService")

class RadioPanel(mediaContext: MediaFrontendContext) :
    MediaTaskPanel(mediaContext, RadioSourceId, null) {

    init {
        Options.isItemDumpingEnabled = true
    }

    internal val sourceClient = RootSourceClient(RadioSourceId)
    private val mediaService = MediaService.createApi(this, frontendContext.iviServiceProvider)

    internal val stationsMediaItem =
        sourceClient.categories.mapToFolderType(EXTRA_RADIO_FOLDER_TYPE_VALUE_STATIONS)
    internal val bandsMediaItems = sourceClient.categories.map {
        it.filter { item -> EXTRA_RADIO_FOLDER_TYPE_VALUE_BANDS == item.folderTypeOrNull() }
    }

    internal fun selectType(type: MediaItem) = sourceClient.browseTo(type)

    internal fun startRadio(id: String) =
        mediaService.launchActionAsync(PlayMediaIdFromSourceAction(RadioSourceId, id))

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(RadioFragment(), this)

    companion object {
        private const val EXTRA_RADIO_FOLDER_TYPE = "android.media.extra.EXTRA_BCRADIO_FOLDER_TYPE"
        private const val EXTRA_RADIO_FOLDER_TYPE_VALUE_STATIONS = 1L
        private const val EXTRA_RADIO_FOLDER_TYPE_VALUE_FAVORITES = 2L
        private const val EXTRA_RADIO_FOLDER_TYPE_VALUE_BANDS = 3L

        private fun LiveData<List<MediaItem>>.mapToFolderType(type: Long) =
            map { it.single { item -> type == item.folderTypeOrNull() } }

        private fun MediaItem.folderTypeOrNull(): Long? =
            metadata?.extras?.getLong(EXTRA_RADIO_FOLDER_TYPE)
    }
}
```

### Example view model

The view model, a [FrontendViewModel](com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel),
transforms the panel's data into information ready to use in a view.

In this example, the `RadioRecyclerViewItem` type represents an entry to display with a standard
Android RecyclerView adapter.

There is a "back" button, which triggers the `onBackPressed` function to dismiss the panel; and a
"Stations list" button, that calls `onStationsButtonClicked` to show the list of stations
recognized by the tuner.

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tomtom.ivi.api.common.media.mediaArtUriOrNull
import com.tomtom.ivi.api.common.media.mediaUriStringOrNull
import com.tomtom.ivi.api.common.media.titleOrNull
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.tools.android.api.livedata.valueUpToDate

class RadioViewModel(panel: RadioPanel) : FrontendViewModel<RadioPanel>(panel) {

    val isLoading = panel.sourceClient.isLoading

    val contents: LiveData<List<MediaGroupItem>> = panel.sourceClient.contents.map { list ->
        list.map { item ->
            MediaEntryGroupItem(
                itemData = panel.policyProvider.extractItemData(item),
                itemState = MutableLiveData(MediaEntryGroupItem.ItemState.IDLE),
                type = TtGroupItem.ItemType.LIST_ITEM,
                clickAction = { item.mediaUriStringOrNull()?.let { panel.startRadio(it) } }
            )
        }
    }

    val availableBands = panel.bandsMediaItems.map { bands -> bands.map { it.titleOrNull() } }
    val selectedBandIndex = MutableLiveData(-1)
        .also {
            it.observe(this) { index ->
                if (index < 0) return@observe
                val mediaItem = panel.bandsMediaItems.valueUpToDate?.get(index) ?: return@observe
                panel.selectType(mediaItem)
            }
        }

    fun onBackPressed() = panel.onBackPressed()
    fun onStationsButtonClicked() = panel.stationsMediaItem.valueUpToDate?.let { panel.selectType(it) }
}
```

### Example fragment

The fragment, based off [IviFragment](com.tomtom.ivi.api.framework.frontend.IviFragment), is mostly
a container for glue code to connect the ViewModel to the XML layout, represented by
`RadioFragmentBinding`; additionally, it links the [SourceClient](com.tomtom.ivi.api.common.media.SourceClient)
used in the panel to the necessary Android [Context](https://developer.android.com/reference/kotlin/android/content/Context).

```kotlin
import android.content.Context
import com.tomtom.ivi.api.framework.frontend.IviFragment
import com.example.radio.databinding.RadioFragmentBinding

class RadioFragment : IviFragment<RadioPanel, RadioViewModel>(RadioViewModel::class) {
    override val viewFactory =
        ViewFactory(RadioFragmentBinding::inflate) { binding ->
            binding.viewModel = viewModel
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        panel.sourceClient.setContext(context)
    }

    override fun onDetach() {
        panel.sourceClient.setContext(null)
        super.onDetach()
    }
}
```

## Additional resources

To create more complex user interfaces, additional facilities are provided:

* [Media visualization components](#media-visualization-components)
* [Panel creation classes](#panel-creation-classes)

### Media visualization components

Standardizing how media contents are displayed is not a simple task; to simplify it, the [MediaItemView](com.tomtom.ivi.api.common.mediasources.MediaItemView)
View has the flexibility to display artwork, title and sub-title for media items in a predictable
way.

[MediaContentView](com.tomtom.ivi.api.common.mediasources.MediaContentView), a [RecyclerView](https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/RecyclerView)
specialization, displays media items according to their type as specified by the Android Automotive
Media APIs. Its item types, defined by the [MediaGroupItem](com.tomtom.ivi.api.common.mediasources.MediaGroupItem)
sealed class, will be displayed together without restriction as dictated by the standard: a part of
the displayed content can be shown as a lists, while another can be laid out in a grid, and headers
can logically separate different groups of contents.

### Panel creation classes

A media-oriented set of classes is available to ease implementation of new panels:
[MediaSourcePanel](com.tomtom.ivi.api.common.mediasources.MediaSourcePanel),
[MediaSourceViewModel](com.tomtom.ivi.api.common.mediasources.MediaSourceViewModel) and
[MediaSourceFragment](com.tomtom.ivi.api.common.mediasources.MediaSourceFragment).

These classes are meant to be used together to create new user experiences while still adhering to
the Android guidelines. These provide a full framework to integrate with [SourceClient](com.tomtom.ivi.api.common.media.SourceClient)s.

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.
