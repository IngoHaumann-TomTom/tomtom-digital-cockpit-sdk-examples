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

package com.tomtom.ivi.example.frontend.account.info

import com.tomtom.ivi.api.framework.frontend.FrontendContext
import com.tomtom.ivi.api.framework.frontend.IviFragment
import com.tomtom.ivi.api.framework.frontend.panels.TaskPanel

// TODO(IVI-5437): Remove dependency on Mode.SIDE_BAR.
@Suppress("DEPRECATION")
class AccountInfoPanel(frontendContext: FrontendContext) :
    TaskPanel(frontendContext, Mode.SIDE_BAR) {

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(AccountInfoFragment(), this)
}
