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

package com.tomtom.ivi.example.frontend.account.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.tools.android.core.livedata.allTrue
import com.tomtom.tools.android.core.livedata.valueUpToDate

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
            accountServiceApi.logInAsync(username, password)
        }
    }
}
