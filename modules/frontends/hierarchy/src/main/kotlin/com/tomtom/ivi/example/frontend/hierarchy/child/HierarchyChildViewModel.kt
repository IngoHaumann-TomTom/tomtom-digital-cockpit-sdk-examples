/*
 * Copyright © 2020 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, then you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.example.frontend.hierarchy.child

import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.frontend.hierarchy.Node

class HierarchyChildViewModel(panel: HierarchyChildPanel) : FrontendViewModel<HierarchyChildPanel>(panel) {

    val selectedItem = panel.node.name

    val children = panel.node.children

    fun onGoToChildClick(child: Node) = panel.openChild(child)
}
