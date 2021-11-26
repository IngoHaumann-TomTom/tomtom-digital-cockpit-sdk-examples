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

import com.tomtom.ivi.example.debugtab.activityview.databinding.DebugActivityviewFragmentBinding
import com.tomtom.ivi.platform.debug.api.frontendextension.debugtab.DebugFragmentTab
import com.tomtom.ivi.platform.debug.api.frontendextension.debugtab.TabbedDebugPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.ModalPanel

/**
 * Fragment for a [DebugFragmentTab] that can open a [ModalPanel].
 *
 * [DebugFragmentTab]s are shown by the [TabbedDebugPanel] in the development debug panel.
 */
class ActivityViewDebugFragmentTab :
    DebugFragmentTab<ActivityViewDebugViewModel>(ActivityViewDebugViewModel::class) {

    override val viewFactory = ViewFactory(DebugActivityviewFragmentBinding::inflate)
}
