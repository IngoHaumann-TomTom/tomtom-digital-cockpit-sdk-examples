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

package com.tomtom.ivi.example.service.accountsettings

import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
import com.tomtom.ivi.api.service.settingsmanagement.SettingScope
import com.tomtom.ivi.api.service.settingsmanagement.SettingUpdateStrategy
import com.tomtom.ivi.api.service.settingsmanagement.StringSettingKey
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.serviceapi.accountsettings.AccountSettingsServiceBase
import com.tomtom.tools.android.api.livedata.valueUpToDate
import kotlinx.serialization.json.Json

class StockAccountSettingsService(iviServiceHostContext: IviServiceHostContext) :
    AccountSettingsServiceBase(iviServiceHostContext) {

    override val settingsKeyPrefix: String = SETTINGS_KEY_PREFIX
    override val settingsVersion: Int = SETTINGS_VERSION

    override suspend fun initActiveAccount(storedSettingsVersion: Int) {
        settingsManagementServiceApi.coCreateSetting(
            serviceId,
            ACTIVE_ACCOUNT_SETTING_KEY,
            "",
            when (storedSettingsVersion) {
                settingsVersion -> SettingUpdateStrategy.NEVER_UPDATE
                else -> SettingUpdateStrategy.ALWAYS_UPDATE
            }
        )

        activeAccount = readActiveAccountFromStorage()
    }

    override suspend fun readActiveAccountFromStorage(): Account? =
        settingsManagementServiceApi
            .coGetSetting(ACTIVE_ACCOUNT_SETTING_KEY)
            .takeIf { it.isNotBlank() }
            ?.let { deserialize(it) }

    override suspend fun writeActiveAccountToStorage(newValue: Account?) {
        with(settingsManagementServiceApi) {
            sessionToken.valueUpToDate?.let { token ->
                val value = newValue?.let { serialize(it) } ?: ""
                coPutSetting(token, ACTIVE_ACCOUNT_SETTING_KEY, value)
            }
        }
    }

    companion object {
        private const val SETTINGS_KEY_PREFIX = "com.tomtom.ivi.example.account"
        private const val SETTINGS_VERSION = 1

        private val ACTIVE_ACCOUNT_SETTING_KEY =
            StringSettingKey(SettingScope.APPLICATION, "$SETTINGS_KEY_PREFIX.activeAccount")

        private fun serialize(account: Account): String =
            Json.encodeToString(AccountSerializer, account)

        private fun deserialize(input: String): Account =
            Json.decodeFromString(AccountSerializer, input)
    }
}

