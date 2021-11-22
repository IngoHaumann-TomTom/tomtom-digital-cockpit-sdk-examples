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

import androidx.lifecycle.map
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FrontendViewModel

class AccountInfoViewModel(panel: AccountInfoPanel) :
    FrontendViewModel<AccountInfoPanel>(panel) {

    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    val displayName = accountServiceApi.activeAccount.map {
        it?.username?.replaceFirstChar(Char::uppercaseChar)
    }

    fun onLogoutClick() = accountServiceApi.logOutAsync()
}
