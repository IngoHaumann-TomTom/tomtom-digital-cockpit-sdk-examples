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

package com.tomtom.ivi.example.frontend.account.info

import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.example.frontend.account.TestData
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals

class AccountInfoViewModelTest : IviTestCase() {
    // Service mock must be configured before a view model is created.
    private val mockAccountService = mockkService(AccountService.Companion::createApi) {
        every { serviceAvailable } returns MutableLiveData(true)
        every { activeAccount } returns MutableLiveData(TestData.testAccount)
        every { loggedInAccounts } returns MutableLiveData(TestData.accountsMap)
    }

    private val mockPanel = niceMockk<AccountInfoPanel>()

    private val sut = AccountInfoViewModel(mockPanel)

    @Test
    fun `username is prepared for display`() {
        assertEquals(TestData.testAccount.username, sut.displayName.getOrAwaitValue())
    }

    @Test
    fun `logout button click calls service`() {
        sut.onLogoutClick()

        verify {
            mockAccountService.logOutAsync(any())
        }
    }
}