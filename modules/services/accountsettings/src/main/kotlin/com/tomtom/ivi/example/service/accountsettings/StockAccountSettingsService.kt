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

package com.tomtom.ivi.example.service.accountsettings

import com.tomtom.ivi.api.framework.configuration.LongDynamicConfigurationKey
import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
import com.tomtom.ivi.api.service.settingsmanagement.LongSettingKey
import com.tomtom.ivi.api.service.settingsmanagement.SettingScope
import com.tomtom.ivi.api.service.settingsmanagement.SettingUpdateStrategy
import com.tomtom.ivi.api.service.settingsmanagement.StringSettingKey
import com.tomtom.ivi.example.account.DynamicConfiguration.onlineLoginValidPeriodInDaysConfigKey
import com.tomtom.ivi.example.account.DynamicConfiguration.settingKeyPrefix
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.serviceapi.accountsettings.AccountSettingsServiceBase
import kotlinx.serialization.json.Json
import java.time.Instant

class StockAccountSettingsService(iviServiceHostContext: IviServiceHostContext) :
    AccountSettingsServiceBase(iviServiceHostContext) {

    override val settingsKeyPrefix: String = settingKeyPrefix
    override val settingsVersion: Int = SETTINGS_VERSION

    // No configuration available, we update the setting manually.
    override val loginTimestampConfigurationKey: LongDynamicConfigurationKey? = null
    override val loginTimestampSettingKey: LongSettingKey = LOGIN_TIMESTAMP_SETTING_KEY

    // The `onlineLoginValidPeriodInDays` setting has default values provided by the dynamic
    // configuration key.
    // All methods for the setting have default implementation.
    override val onlineLoginValidPeriodInDaysConfigurationKey: LongDynamicConfigurationKey =
        onlineLoginValidPeriodInDaysConfigKey

    override val onlineLoginValidPeriodInDaysSettingKey: LongSettingKey =
        onlineLoginValidPeriodInDaysConfigurationKey.toSettingKey(
            SettingScope.APPLICATION,
            settingKeyPrefix
        )

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

        activeAccount = loadSettingIfAvailable(
            storedSettingsVersion,
            { readActiveAccountFromStorage() },
            { null }
        )
    }

    override suspend fun readActiveAccountFromStorage(): Account? =
        settingsManagementServiceApi
            .coGetSetting(ACTIVE_ACCOUNT_SETTING_KEY)
            .takeIf { it.isNotBlank() }
            ?.let { deserialize(it) }

    override suspend fun writeActiveAccountToStorage(newValue: Account?) {
        settingsWriter { sessionToken ->
            val value = newValue?.let { serialize(it) } ?: ""
            coPutSetting(sessionToken, ACTIVE_ACCOUNT_SETTING_KEY, value)
        }

        updateLoginTimestamp(newValue?.let { Instant.now().epochSecond } ?: 0L)
    }

    private suspend fun updateLoginTimestamp(timestamp: Long) {
        settingsWriter { sessionToken ->
            coPutSetting(
                sessionToken,
                LOGIN_TIMESTAMP_SETTING_KEY,
                timestamp
            )
        }

        loginTimestamp = timestamp
    }

    override suspend fun initLoginTimestamp(storedSettingsVersion: Int) {
        settingsManagementServiceApi.coCreateSetting(
            serviceId,
            loginTimestampSettingKey,
            0L,
            SettingUpdateStrategy.ALWAYS_UPDATE
        )

        loginTimestamp = loadSettingIfAvailable(
            storedSettingsVersion,
            { readLoginTimestampFromStorage() },
            { 0L }
        )
    }

    companion object {
        private const val SETTINGS_VERSION = 2

        private val ACTIVE_ACCOUNT_SETTING_KEY =
            StringSettingKey(SettingScope.APPLICATION, "$settingKeyPrefix.activeAccount")
        private val LOGIN_TIMESTAMP_SETTING_KEY =
            LongSettingKey(SettingScope.USER_PROFILE, "$settingKeyPrefix.loginTimestamp")

        private fun serialize(account: Account): String =
            Json.encodeToString(AccountSerializer, account)

        private fun deserialize(input: String): Account =
            Json.decodeFromString(AccountSerializer, input)
    }
}

