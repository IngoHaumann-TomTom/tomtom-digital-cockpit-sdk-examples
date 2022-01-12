/*
 * Copyright © 2021 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.example.debugtab.activityview

import androidx.lifecycle.map
import com.tomtom.ivi.platform.debug.api.frontendextension.debugtab.TabbedDebugPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.ModalPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.Panel
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FrontendViewModel

/**
 * View model for a debug tab.
 *
 * The [panel] variable allows access to [TabbedDebugPanel.panelManager], which contains
 * facilities to open and close arbitrary [Panel]s.
 */
internal class ActivityViewDebugViewModel(panel: TabbedDebugPanel) :
    FrontendViewModel<TabbedDebugPanel>(panel) {

    val isButtonEnabled = panel.panelManager.frontendPanels.map { panels ->
        panels.none { it is ModalPanel }
    }

    fun onButtonClicked() {
        panel.panelManager.addPanel(ModalActivityPanel(panel.frontendContext))
    }
}
