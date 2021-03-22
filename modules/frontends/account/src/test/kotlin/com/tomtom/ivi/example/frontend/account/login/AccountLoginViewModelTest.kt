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
import com.tomtom.ivi.example.serviceapi.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountId
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import io.mockk.every
import io.mockk.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

class AccountLoginViewModelTest : IviTestCase() {

    // Service mock must be configured before a view model is created.
    private val mutableServiceAvailable = MutableLiveData(true)
    private val mockAccountService = mockkService(AccountService.Companion::createApi) {
        every { serviceAvailable } returns mutableServiceAvailable
        every { activeAccount } returns MutableLiveData(ACCOUNT)
        every { accounts } returns MutableLiveData(
            mapOf(ACCOUNTID to ACCOUNT)
        )
    }

    private val mockPanel = niceMockk<AccountLoginPanel>()

    private val sut = AccountLoginViewModel(mockPanel)

    @Test
    fun `login button is disabled if username and password field are empty`() {
        assertFalse(sut.isLoginEnabled.getOrAwaitValue())
    }

    @Test
    fun `login button is disabled if password field is empty`() {
        sut.username.value = USERNAME
        assertFalse(sut.isLoginEnabled.getOrAwaitValue())
    }

    @Test
    fun `login button is disabled if username field is empty`() {
        sut.password.value = PASSWORD
        assertFalse(sut.isLoginEnabled.getOrAwaitValue())
    }

    @Test
    fun `login button is enabled if username and password are correct`() {
        sut.username.value = USERNAME
        sut.password.value = PASSWORD
        assertTrue(sut.isLoginEnabled.getOrAwaitValue())
    }

    @Test
    fun `login button is disabled if service is not available`() {
        sut.username.value = USERNAME
        sut.password.value = PASSWORD
        mutableServiceAvailable.value = false

        assertFalse(sut.isLoginEnabled.getOrAwaitValue())
    }

    @Test
    fun `login button click calls service`() {
        sut.username.value = USERNAME
        sut.password.value = PASSWORD

        sut.onLoginClick()

        verify {
            mockAccountService.logInAsync(USERNAME, PASSWORD, any())
        }
    }

    companion object {
        private const val USERNAME = "testUser"
        private const val PASSWORD = "testPassword"

        private val ACCOUNTID = AccountId("ACCOUNTID")
        private val ACCOUNT = Account(ACCOUNTID, USERNAME)

    }
}