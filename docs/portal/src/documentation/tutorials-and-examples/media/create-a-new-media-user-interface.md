---
title: Create a New Media User Interface
---

The TomTom IndiGO Application Suite provides a default user interface (UI). This user interface
might not be suitable for a media app (also called "media source"), when it does not conform to the
typical organization of content, or when their offered content can not be liberally browsed or
played or controlled.
Such sources would look disappointing or completely unusable in the default user interface from the
TomTom IndiGO Application Suite.

A few examples of media apps which are not suitable for the default user interface:

- A controller/player for an AM/FM/DAB+ radio tuner installed in the system:
  The default user interface would show the bands, stations, and favorites lumped together as tabs,
  and dump together all known stations in a single tab.
- An app which can only stream music (for example via Bluetooth, or via Wi-Fi):
  No content would be available for browsing, without explanation.
- An app that only displays remotely broadcast video streams:
  The default user interface is not suitable for video playback, and if the app would only receive
  broadcast video streams, no content would be available for browsing, with no explanation.

If such drastic customizations are not necessary, for example if the only needs are fixing up how
the content from a media app is displayed to the user, or to add an icon to perform an action
specific to that app, using the default media user interface should be preferred and
[appropriately customized](/tomtom-indigo/documentation/tutorials-and-examples/media/customize-a-media-source).

For this guide, knowledge of the TomTom IndiGO [`appsuite_media_api_common_core`](TTIVI_INDIGO_API) and
[`appsuite_media_api_common_frontend`](TTIVI_INDIGO_API) modules will greatly help. In the
[media overview](/tomtom-indigo/documentation/development/platform-domains/media), more details can be
found over the Android Automotive Media framework and how TomTom IndiGO uses it.

## Concepts

The guide will implement the user interface for a simple radio made available in the system through
the standard [Android Automotive Radio API](https://source.android.com/devices/automotive/radio).
Through the standard API for media, an Android
[media browser service](https://developer.android.com/reference/kotlin/androidx/media/MediaBrowserServiceCompat)
will provide access to a hardware radio tuner module.

This user interface will be a new panel. A panel is composed by three classes: a
[`TaskPanel`](TTIVI_INDIGO_API) to define the logic; a view
model, [`FrontendViewModel`](TTIVI_INDIGO_API),
potentially using data binding; an [`IviFragment`](TTIVI_INDIGO_API)
defining the Android fragment and creating the instance of the view model.
Please refer to the
[frontend plugin guide](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
to get started.

This user interface will use a specialization of the base user interface panel type
[`TaskPanel`](TTIVI_INDIGO_API),
[`MediaTaskPanel`](TTIVI_INDIGO_API). This panel type is more
suitable for media apps, as it contains media-specific facilities.

This being merely an example, the user interface is very sparse and only contains one panel to
display, and no functionality other than basic browsing and playing.

### Example panel

An application that implements the concepts presented here is provided in the
`examples/media/userflowpolicy` directory.

The example's panel class, based on [`MediaTaskPanel`](TTIVI_INDIGO_API), enables browsing through the
stations recognized by the radio and the raw frequencies offered by each radio band.

In the panel the media [`Options`](TTIVI_INDIGO_API) are used to print out to
the device logcat all media items retrieved by all media services: this helps __only__ in the
initial discovery phase to analyze the format of all content returned by the media source. This
setting __must not__ be used in production, and will not function in _release_ builds.

The [`RootSourceClient`](TTIVI_INDIGO_API) is used to browse the content and retrieve the available
categories (in the case of radio, those are bands and stations), while the
[`MediaService`](TTIVI_INDIGO_API) lets the user play a radio.

[`src/main/kotlin/com/example/ivi/example/media/userflowpolicy/ExampleMediaSourcePanel.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/media/userflowpolicy/src/main/kotlin/com/example/ivi/example/media/userflowpolicy/ExampleMediaSourcePanel.kt#L38-L74)

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.tomtom.ivi.appsuite.media.api.common.core.IviMediaItem
import com.tomtom.ivi.appsuite.media.api.common.core.Options
import com.tomtom.ivi.appsuite.media.api.common.core.RootSourceClient
import com.tomtom.ivi.appsuite.media.api.common.core.SourceId
import com.tomtom.ivi.appsuite.media.api.common.core.actions.standard.PlayMediaIdFromSourceAction
import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaTaskPanel
import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaFrontendContext
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.PolicyProvider
import com.tomtom.ivi.appsuite.media.api.service.core.MediaService
import com.tomtom.ivi.appsuite.media.api.service.core.createApi
import com.tomtom.ivi.platform.frontend.api.common.frontend.IviFragment

// This is an example source ID, matching the default Android Automotive car radio service.
internal object RadioSourceId :
    SourceId("com.android.car.radio", "com.android.car.radio.service.RadioAppService")

internal class RadioPanel(mediaContext: MediaFrontendContext) :
    MediaTaskPanel(mediaContext, RadioSourceId, null) {

    val policyProvider: PolicyProvider =
        mediaFrontendContext.mediaConfiguration.getPolicyProvider(RadioSourceId)

    val sourceClient = RootSourceClient(RadioSourceId)
    private val mediaService = MediaService.createApi(this, frontendContext.iviServiceProvider)

    val stationsMediaItem =
        sourceClient.categories.mapToFolderType(EXTRA_RADIO_FOLDER_TYPE_VALUE_STATIONS)
    val bandsMediaItems = sourceClient.categories.map {
        it.filter { item -> EXTRA_RADIO_FOLDER_TYPE_VALUE_BANDS == item.folderTypeOrNull() }
    }

    init {
        Options.isItemDumpingEnabled = true
    }

    internal fun selectType(type: IviMediaItem) = sourceClient.browseTo(type)

    internal fun startRadio(id: String) =
        mediaService.launchActionAsync(PlayMediaIdFromSourceAction(RadioSourceId, id))

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(RadioFragment(), this)

    companion object {
        private const val EXTRA_RADIO_FOLDER_TYPE = "android.media.extra.EXTRA_BCRADIO_FOLDER_TYPE"
        private const val EXTRA_RADIO_FOLDER_TYPE_VALUE_STATIONS = 1L
        private const val EXTRA_RADIO_FOLDER_TYPE_VALUE_FAVORITES = 2L
        private const val EXTRA_RADIO_FOLDER_TYPE_VALUE_BANDS = 3L

        private fun LiveData<List<IviMediaItem>>.mapToFolderType(type: Long) =
            map { it.single { item -> type == item.folderTypeOrNull() } }

        private fun IviMediaItem.folderTypeOrNull(): Long? =
            getLong(EXTRA_RADIO_FOLDER_TYPE)
    }
}
```

### Example view model

The view model, a [`FrontendViewModel`](TTIVI_INDIGO_API), transforms the panel's data into
information ready to use in a view.

In this example, the `RadioRecyclerViewItem` type represents an entry to display with a standard
Android RecyclerView adapter.

There is a "back" button, which triggers the `onBackPressed` function to dismiss the panel; and a
"Stations list" button, that calls `onStationsButtonClicked` to show the list of stations
recognized by the tuner.

[`src/main/kotlin/com/example/ivi/example/media/userflowpolicy/ExampleMediaSourceViewModel.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/media/userflowpolicy/src/main/kotlin/com/example/ivi/example/media/userflowpolicy/ExampleMediaSourceViewModel.kt#L31-L56)

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaEntryGroupItem
import com.tomtom.ivi.appsuite.media.api.common.frontend.viewmodel.RadioPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FrontendViewModel
import com.tomtom.tools.android.api.livedata.valueUpToDate
import com.tomtom.tools.android.api.uicontrols.recyclerview.group.ListGroupItem

internal class RadioViewModel(panel: RadioPanel) : FrontendViewModel<RadioPanel>(panel) {

    val isLoading = panel.sourceClient.isLoading

    val contents: LiveData<List<ListGroupItem>> = panel.sourceClient.contents.map { list ->
        list.map { item ->
            MediaEntryGroupItem(
                itemData = panel.policyProvider.extractItemDataPolicy(item),
                itemState = MutableLiveData(MediaEntryGroupItem.ItemState.IDLE),
                type = ListGroupItem.ItemType.LIST_ITEM,
                clickAction = { item.mediaUri?.toString()?.let { panel.startRadio(it) } }
            )
        }
    }

    val availableBands = panel.bandsMediaItems.map { bands -> bands.map { it.title } }
    val selectedBandIndex = MutableLiveData(-1)
        .also {
            it.observe(this) { index ->
                if (index < 0) return@observe
                val mediaItem = panel.bandsMediaItems.valueUpToDate?.get(index) ?: return@observe
                panel.selectType(mediaItem)
            }
        }

    fun onBackPressed() = panel.onBackPressed()
    fun onStationsButtonClicked() =
        panel.stationsMediaItem.valueUpToDate?.let { panel.selectType(it) }
}
```

### Example fragment

The fragment, inheriting from [`IviFragment`](TTIVI_INDIGO_API), is mostly
a container for glue code to connect the ViewModel to the XML layout, represented by
`RadioFragmentBinding`; additionally, it links the
[`SourceClient`](TTIVI_INDIGO_API)
used in the panel to the necessary Android
[`Context`](https://developer.android.com/reference/kotlin/android/content/Context).

[`src/main/kotlin/com/example/ivi/example/media/userflowpolicy/ExampleMediaSourceFragment.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/media/userflowpolicy/src/main/kotlin/com/example/ivi/example/media/userflowpolicy/ExampleMediaSourceFragment.kt#L24-L45)

```kotlin
import android.content.Context
import com.tomtom.ivi.platform.frontend.api.common.frontend.IviFragment
import com.example.radio.databinding.RadioFragmentBinding

internal class RadioFragment : IviFragment<RadioPanel, RadioViewModel>(RadioViewModel::class) {
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

- [Media visualization components](#media-visualization-components)
- [Media view model components](#media-view-model-components)

### Media visualization components

Standardizing how media contents are displayed is not a simple task; to simplify it,
[`MediaContentView`](TTIVI_INDIGO_API), an Android
[`RecyclerView`](https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/RecyclerView)
specialization, displays media items according to their type as specified by the Android Automotive
Media APIs. Its item types, defined by the [`MediaGroupItem`](TTIVI_INDIGO_API)
sealed class, will be displayed together without restriction as dictated by the standard: a part of
the displayed content can be shown as a list, while another can be laid out in a grid, and headers
can logically separate different groups of contents. Both the list and the grid items display
the artwork, title, and sub-title for media items in a predictable way.

### Media view model components

You can easily create views that display playback information and/or contain media controls by using
the media-oriented set of view models:
[`MediaPlaybackViewModel`](TTIVI_INDIGO_API), [`TouchTrackViewModel`](TTIVI_INDIGO_API), and
[`MediaButtonsViewModel`](TTIVI_INDIGO_API).
