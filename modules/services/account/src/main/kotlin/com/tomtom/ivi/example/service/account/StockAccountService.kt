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
import com.tomtom.ivi.example.account.StaticConfiguration.onlineAccountEndpointConfigKey
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBase
import com.tomtom.ivi.example.serviceapi.account.SensitiveString
import com.tomtom.ivi.example.serviceapi.accountsettings.AccountSettingsService
import com.tomtom.ivi.example.serviceapi.accountsettings.createApi
import com.tomtom.tools.android.api.livedata.requireValue
import com.tomtom.tools.android.api.livedata.valueUpToDate
import java.time.Instant
import java.time.temporal.ChronoUnit


class StockAccountService(iviServiceHostContext: IviServiceHostContext) :
    AccountServiceBase(iviServiceHostContext) {

    private val onlineAccountEndpoint =
        iviServiceHostContext.staticConfigurationProvider[onlineAccountEndpointConfigKey]

    private val settingsServiceApi = AccountSettingsService.createApi(this, iviServiceProvider)

    private val mutableLoggedInAccounts = MutableMirrorableMap<Uid<Account>, Account>()

    override fun onCreate() {
        super.onCreate()

        loggedInAccounts = mutableLoggedInAccounts

        // Executes an action once on the service becoming available.
        settingsServiceApi.queueOrRun { service ->
            // Stored account could be logged in a while ago, lets check if it is still valid.
            val daysSinceLogin = Instant.ofEpochSecond(service.loginTimestamp.requireValue())
                .until(Instant.now(), ChronoUnit.DAYS)
            if (daysSinceLogin >= service.onlineLoginValidPeriodInDays.requireValue()) {
                service.updateActiveAccountAsync(null)
            } else {
                service.activeAccount.valueUpToDate?.let { account ->
                    mutableLoggedInAccounts[account.accountUid] = account
                    activeAccount = account
                }
            }
        }

        // The account service cannot run without the account settings service.
        settingsServiceApi.serviceAvailable.observe(this) {
            serviceReady = it
        }
    }

    override suspend fun logIn(username: String, password: SensitiveString): Boolean {
        val account = mutableLoggedInAccounts.values.find { it.username == username }
            ?: logInOnline(username, password)?.also {
                mutableLoggedInAccounts[it.accountUid] = it
            }

        account?.let {
            activeAccount = it
            settingsServiceApi.coUpdateActiveAccount(activeAccount)
        }

        return account != null
    }

    override suspend fun logOut() {
        activeAccount?.let {
            mutableLoggedInAccounts.remove(it.accountUid)
            activeAccount = null
            settingsServiceApi.coUpdateActiveAccount(null)
        }
    }

    private fun logInOnline(username: String, password: SensitiveString): Account? =
        takeIf { isValidUsername(username) && isValidPassword(password.value) }?.run {
            // Pretend we make an online request.
            onlineAccountEndpoint
            Account(username)
        }

    companion object {
        private fun isValidUsername(value: String) = value.trim().length > 1
        private fun isValidPassword(value: String) = value.trim().length > 2
    }
}