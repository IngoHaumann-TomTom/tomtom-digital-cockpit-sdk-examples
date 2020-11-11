/*
 * Copyright (c) 2020 - 2020 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.ivi.example.frontend.account

import com.tomtom.ivi.api.framework.frontend.Frontend
import com.tomtom.ivi.api.framework.frontend.FrontendContext
import com.tomtom.ivi.api.framework.frontend.FrontendFactory
import com.tomtom.ivi.example.frontend.account.info.AccountInfoPanel
import com.tomtom.ivi.example.frontend.account.login.AccountLoginPanel

class AccountFrontend(frontendContext: FrontendContext) : Frontend(frontendContext) {

    override fun createMainTaskPanel() = AccountLoginPanel(frontendContext, this)

    internal fun showAccountInfoPanel(displayName: String) {
        closeAllTaskPanels()
        addPanel(AccountInfoPanel(frontendContext, displayName, this))
    }

    class Factory : FrontendFactory {
        override fun createFrontend(frontendContext: FrontendContext) =
            AccountFrontend(frontendContext)
    }
}
