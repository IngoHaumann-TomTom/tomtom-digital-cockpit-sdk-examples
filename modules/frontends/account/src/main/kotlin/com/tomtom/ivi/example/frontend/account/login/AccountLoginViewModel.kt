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

package com.tomtom.ivi.example.frontend.account.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.SensitiveString
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.tools.android.api.livedata.allTrue
import com.tomtom.tools.android.api.livedata.valueUpToDate

class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    val username = MutableLiveData("")
    val password = MutableLiveData("")

    val isLoginEnabled = allTrue(
        accountServiceApi.serviceAvailable,
        username.map { it.isNotBlank() },
        password.map { it.isNotBlank() }
    )

    fun onLoginClick() {
        isLoginEnabled.valueUpToDate?.takeIf { it }?.let {
            val username = username.value ?: return
            val password = password.value ?: return
            accountServiceApi.logInAsync(username, SensitiveString(password))
        }
    }
}
