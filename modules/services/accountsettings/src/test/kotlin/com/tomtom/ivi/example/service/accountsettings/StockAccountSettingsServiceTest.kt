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

import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.common.uid.Uid
import com.tomtom.ivi.api.service.settingsmanagement.IntSettingKey
import com.tomtom.ivi.api.service.settingsmanagement.SettingsManagementService
import com.tomtom.ivi.api.service.settingsmanagement.SettingsManagementSessionToken
import com.tomtom.ivi.api.service.settingsmanagement.StringSettingKey
import com.tomtom.ivi.api.service.settingsmanagement.createApi
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StockAccountSettingsServiceTest : IviTestCase() {
    private val mockSettingsManagementService =
        mockkService(SettingsManagementService.Companion::createApi) {
            every { serviceAvailable } returns MutableLiveData(true)
            every { sessionToken } returns MutableLiveData(SettingsManagementSessionToken(Uid.new()))
            coJustRun { coCreateSetting(any(), any<IntSettingKey>(), any(), any()) }
            coJustRun { coCreateSetting(any(), any<StringSettingKey>(), any(), any()) }
            coEvery { coGetSetting(any<IntSettingKey>()) } returns 1
            coEvery { coGetSetting(any<StringSettingKey>()) } returns ""
            coEvery { coPutSetting(any(), any<IntSettingKey>(), any()) } returns true
            coEvery { coPutSetting(any(), any<StringSettingKey>(), any()) } returns true
        }

    private fun createSut() = StockAccountSettingsService(niceMockk()).also { it.onCreate() }

    @Test
    fun `with no stored active account`() {
        // GIVEN No stored active account.

        // WHEN The account settings service is created.
        val sut = createSut()

        // THEN The active account is not set.
        assertNull(sut.activeAccount)
    }

    @Test
    fun `with stored active account`() {
        // GIVEN The stored active account.
        val account = Account("username")

        coEvery { mockSettingsManagementService.coGetSetting(any<StringSettingKey>()) } returns
                Json.encodeToString(AccountSerializer, account)

        // WHEN The account settings service is created.
        val sut = createSut()

        // THEN The active account is set.
        assertEquals(account, sut.activeAccount)
    }

    @Test
    fun `on updating active account with a value`() {
        // GIVEN The account settings service has no active account.
        val account = Account("username")
        val sut = createSut()

        // WHEN An active account is set.
        runBlocking {
            sut.updateActiveAccount(account)
        }

        // THEN The active account is stored.
        coVerify {
            mockSettingsManagementService.coPutSetting(
                any(),
                any(),
                Json.encodeToString(AccountSerializer, account)
            )
        }
    }

    @Test
    fun `on updating active account with null`() {
        // GIVEN The account settings service has an active account.
        val account = Account("username")
        val sut = createSut()

        runBlocking {
            sut.updateActiveAccount(account)
        }
        clearAllMocks(answers = false)

        // WHEN An active account is reset.
        runBlocking {
            sut.updateActiveAccount(null)
        }

        // THEN The active account is reset in storage.
        coVerify {
            mockSettingsManagementService.coPutSetting(
                any(),
                any(),
                ""
            )
        }
    }
}