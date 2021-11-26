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

import com.tomtom.ivi.platform.debug.api.frontendextension.debugtab.DebugTabFrontendExtension
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendExtension
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.DebugPanel

/**
 * This [FrontendExtension] adds a new tab for the [DebugPanel] of the example app.
 * The new debug tab is added to the stock list of tabs by default.
 *
 * This showcases the addition of debug panel tabs, but also modal panels and 3rd party activity
 * embedding.
 */
val activityViewDebugTabFrontendExtension: FrontendExtension =
    DebugTabFrontendExtension(
        tabClass = ActivityViewDebugFragmentTab::class,
        titleResourceId = R.string.ttivi_debug_activityview_title
    )
