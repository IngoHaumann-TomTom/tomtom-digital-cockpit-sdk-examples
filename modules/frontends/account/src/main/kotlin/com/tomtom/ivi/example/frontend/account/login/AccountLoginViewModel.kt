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

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel

class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    val username = MutableLiveData("")
    val password = MutableLiveData("")

    val isLoginEnabled = MediatorLiveData<Boolean>().apply {
        addSource(username) { setValue(validateUsername() && validatePassword()) }
        addSource(password) { setValue(validateUsername() && validatePassword()) }
    }

    fun onLoginClick() {
        username.value
            ?.takeIf { isLoginEnabled.value == true }
            ?.let { it.first().toUpperCase() + it.substring(1) }
            ?.let { panel.frontend.showAccountInfoPanel(it) }
    }

    private fun validateUsername() =
        (username.value?.trim()?.length ?: 0) > 1

    private fun validatePassword() =
        (password.value?.trim()?.length ?: 0) > 2
}