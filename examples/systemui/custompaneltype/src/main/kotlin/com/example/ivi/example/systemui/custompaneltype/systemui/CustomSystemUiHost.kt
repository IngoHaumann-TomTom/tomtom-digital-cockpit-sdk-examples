package com.example.ivi.example.systemui.custompaneltype.systemui

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ivi.example.systemui.custompaneltype.common.CustomSystemUiPanel
import com.example.ivi.example.systemui.custompaneltype.databinding.TtiviCustompaneltypeCustomsystemuiBinding
import com.tomtom.ivi.platform.framework.api.common.iviinstance.IviInstanceId
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendMetadata
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.ControlCenterPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.DebugPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.GuidancePanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.HomePanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.MainMenuPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.ModalPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.NotificationPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.OverlayPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.PanelTypeSet
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.ProcessBarPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.SearchPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.TaskPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.TaskProcessBarPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.panelTypeSetOf
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FixedConstructorFactory
import com.tomtom.ivi.platform.systemui.api.common.systemuihost.SystemUiHost
import com.tomtom.ivi.platform.systemui.api.common.systemuihost.containercontrollers.TaskPanelContainerController
import com.tomtom.tools.android.core.animation.LifecycleAwareAnimationController

/**
 * The system UI host is the overarching class of the system UI. It's responsible for creating the
 * [viewModel], and for creating the view through the [viewFactory].
 *
 * @see https://developer.tomtom.com/tomtom-indigo/documentation/development/system-ui
 */
internal class CustomSystemUiHost(
    fragmentActivity: FragmentActivity,
    iviInstanceId: IviInstanceId,
    frontendMetadata: Collection<FrontendMetadata>
) : SystemUiHost(fragmentActivity, iviInstanceId, frontendMetadata) {

    private lateinit var viewModel: CustomSystemUiViewModel

    override val viewFactory: ViewFactory =
        BindingViewFactory(TtiviCustompaneltypeCustomsystemuiBinding::inflate, ::bindSystemUiView)

    override val supportedPanelTypes: PanelTypeSet = panelTypeSetOf(
        CustomSystemUiPanel::class,
        TaskPanel::class,
        TaskProcessBarPanel::class,
    )

    override val unsupportedPanelTypes: PanelTypeSet = panelTypeSetOf(
        ControlCenterPanel::class,
        DebugPanel::class,
        GuidancePanel::class,
        HomePanel::class,
        MainMenuPanel::class,
        ModalPanel::class,
        NotificationPanel::class,
        OverlayPanel::class,
        ProcessBarPanel::class,
        SearchPanel::class
    )

    override fun onCreate() {
        viewModel = ViewModelProvider(
            viewModelStoreOwner,
            FixedConstructorFactory(coreViewModel)
        )[CustomSystemUiViewModel::class.java]
    }

    private fun bindSystemUiView(binding: TtiviCustompaneltypeCustomsystemuiBinding) {
        binding.viewModel = viewModel
        binding.panelRegistry = viewModel.panelRegistry

        val animationController = LifecycleAwareAnimationController(viewLifecycleOwner)

        val taskPanelContainerController = TaskPanelContainerController(
            ExampleTaskPanelSubContainerManager(animationController),
            binding.exampleSystemuiTaskPanelContainer,
            viewModel.panelRegistry.iviPanelRegistry.taskPanelStack,
            createPanelContext()
        )

        register(taskPanelContainerController)
        registerOnBackPressedConsumer(taskPanelContainerController)
    }
}
