/*
 * Copyright © 2020 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.example.frontend.hierarchy.root

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.tomtom.ivi.example.frontend.hierarchy.Node
import com.tomtom.ivi.example.frontend.hierarchy.R
import com.tomtom.ivi.example.frontend.hierarchy.child.HierarchyChildPanel
import com.tomtom.ivi.example.frontend.hierarchy.leaf.HierarchyLeafPanel
import com.tomtom.ivi.platform.framework.api.common.annotations.IviExperimental
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendContext
import com.tomtom.ivi.platform.frontend.api.common.frontend.IviFragment
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.AnyPanel
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.PanelDescriptor
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.TaskPanel
import com.tomtom.tools.android.api.resourceresolution.drawable.ResourceDrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.ResourceStringResolver

@OptIn(IviExperimental::class)
internal typealias AddPanel = (panel: AnyPanel) -> Unit

/**
 * The panel at the root of the hierarchy.
 */
internal class HierarchyRootPanel(frontendContext: FrontendContext, private val addPanel: AddPanel) :
    TaskPanel(frontendContext) {

    @OptIn(IviExperimental::class)
    override val descriptor: LiveData<PanelDescriptor>
        get() = super.descriptor.map {
            it.copy(
                label = ResourceStringResolver(R.string.ttivi_hierarchy_rootpanel),
                icon = ResourceDrawableResolver(R.drawable.ttivi_hierarchy_rootpanel_icon)
            )
        }

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(HierarchyRootFragment(), this)

    fun openChild(child: Node) {
        if (child.children.isEmpty()) {
            addPanel(HierarchyLeafPanel(frontendContext, child))
        } else {
            addPanel(HierarchyChildPanel(frontendContext, child, addPanel))
        }
    }
}
