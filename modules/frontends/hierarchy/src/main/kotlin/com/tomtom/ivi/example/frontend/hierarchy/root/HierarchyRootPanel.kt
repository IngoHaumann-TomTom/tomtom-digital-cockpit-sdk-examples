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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.framework.frontend.FrontendContext
import com.tomtom.ivi.api.framework.frontend.IviFragment
import com.tomtom.ivi.api.framework.frontend.panels.Panel
import com.tomtom.ivi.api.framework.frontend.panels.TaskPanel
import com.tomtom.ivi.example.frontend.hierarchy.Node
import com.tomtom.ivi.example.frontend.hierarchy.R
import com.tomtom.ivi.example.frontend.hierarchy.child.HierarchyChildPanel
import com.tomtom.ivi.example.frontend.hierarchy.leaf.HierarchyLeafPanel
import com.tomtom.tools.android.api.resourceresolution.drawable.DrawableResolver
import com.tomtom.tools.android.api.resourceresolution.drawable.ResourceDrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.ResourceStringResolver
import com.tomtom.tools.android.api.resourceresolution.string.StringResolver

typealias AddPanel = (panel: Panel) -> Unit

/**
 * The panel at the root of the hierarchy.
 */
class HierarchyRootPanel(frontendContext: FrontendContext, private val addPanel: AddPanel)
    : TaskPanel(frontendContext, Mode.MAXIMIZED) {

    override val label: LiveData<StringResolver> =
        MutableLiveData(ResourceStringResolver(R.string.frontend_hierarchy_root_panel))

    override val icon: LiveData<DrawableResolver?> = MutableLiveData(
        ResourceDrawableResolver(R.drawable.frontend_hierarchy_root_panel_icon)
    )

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
