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

package com.tomtom.ivi.example.frontend.hierarchy.leaf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.framework.frontend.FrontendContext
import com.tomtom.ivi.api.framework.frontend.IviFragment
import com.tomtom.ivi.api.framework.frontend.panels.TaskPanel
import com.tomtom.ivi.example.frontend.hierarchy.Node
import com.tomtom.tools.android.api.resourceresolution.string.StaticStringResolver
import com.tomtom.tools.android.api.resourceresolution.string.StringResolver

/**
 * Displays a [node] that is at the leaf of the tree, meaning it has no children.
 */
class HierarchyLeafPanel(frontendContext: FrontendContext, val node: Node)
    : TaskPanel(frontendContext, Mode.MAXIMIZED) {

    override val label: LiveData<StringResolver> =
        MutableLiveData(StaticStringResolver(node.name))

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(HierarchyLeafFragment(), this)
}
