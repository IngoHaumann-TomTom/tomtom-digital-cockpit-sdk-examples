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

package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.api.common.uid.Uid
import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
import com.tomtom.ivi.api.framework.iviservice.mirrormap.MutableMirrorableMap
import com.tomtom.ivi.api.framework.iviservice.queueOrRun
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBase
import com.tomtom.ivi.example.serviceapi.accountsettings.AccountSettingsService
import com.tomtom.ivi.example.serviceapi.accountsettings.createApi
import com.tomtom.tools.android.core.livedata.valueUpToDate

// TODO(IVI-3049): Add integration tests.
class StockAccountService(iviServiceHostContext: IviServiceHostContext) :
    AccountServiceBase(iviServiceHostContext) {

    private val settingsServiceApi = AccountSettingsService.createApi(this, iviServiceProvider)

    private val mutableAccounts = MutableMirrorableMap<Uid<Account>, Account>()

    override fun onCreate() {
        super.onCreate()

        accounts = mutableAccounts

        // Executes an action once on the service becoming available.
        settingsServiceApi.queueOrRun { service ->
            service.activeAccount.valueUpToDate?.let { account ->
                mutableAccounts[account.accountUid] = account
                activeAccount = account
            }
        }
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun logIn(username: String, password: String): Boolean =
        null != takeIf { isValidUsername(username) && isValidPassword(password) }
            ?.run {
                activeAccount = mutableAccounts.values.find { it.username == username }
                    ?: run {
                        Account(username).also { mutableAccounts[it.accountUid] = it }
                    }
                settingsServiceApi.coUpdateActiveAccount(activeAccount)
            }

    override suspend fun logOut() {
        activeAccount = null
        settingsServiceApi.coUpdateActiveAccount(activeAccount)
    }

    companion object {
        private fun isValidUsername(value: String) = value.trim().length > 1
        private fun isValidPassword(value: String) = value.trim().length > 2
    }
}