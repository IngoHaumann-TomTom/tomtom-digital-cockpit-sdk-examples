/*
 * Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.ivi.example.frontend.hierarchy.root

import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.frontend.hierarchy.Node

class HierarchyRootViewModel(panel: HierarchyRootPanel) : FrontendViewModel<HierarchyRootPanel>(panel) {

    val children = listOf(
        Node("One", listOf(Node("One A"))),
        Node("Two"),
        Node("Three")
    )

    fun onGoToChildClick(child: Node) = panel.openChild(child)
}
