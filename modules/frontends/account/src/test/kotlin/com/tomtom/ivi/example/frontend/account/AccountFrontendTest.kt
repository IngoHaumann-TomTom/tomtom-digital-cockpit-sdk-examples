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

package com.tomtom.ivi.example.frontend.account

import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.frontend.account.info.AccountInfoPanel
import com.tomtom.ivi.example.frontend.account.login.AccountLoginPanel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.platform.frontend.api.common.frontend.panels.filterPanels
import com.tomtom.ivi.platform.tools.api.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import io.mockk.every
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

internal class AccountFrontendTest : IviTestCase() {
    private val mutableAccount = MutableLiveData<Account?>(null).also {
        // Service mock must be configured before a frontend is created.
        mockkService(AccountService.Companion::createApi) {
            every { serviceAvailable } returns MutableLiveData(true)
            every { activeAccount } returns it
            every { loggedInAccounts } returns MutableLiveData(TestData.accountsMap)
        }
    }

    private val sut = AccountFrontend(niceMockk()).apply { onCreate() }

    @Test
    fun `frontend does not open a panel after an user has logged in`() {
        // GIVEN no user is logged in.
        mutableAccount.value = null

        // WHEN an user has logged in.
        mutableAccount.value = TestData.testAccount

        // THEN the frontend does not open a panel.
        assertTrue(sut.panels.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `frontend does not open a panel after an user has logged out`() {
        // GIVEN an user is logged in.
        mutableAccount.value = TestData.testAccount

        // WHEN the user has logged out
        mutableAccount.value = null

        // THEN the frontend does not open a panel.
        assertTrue(sut.panels.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `frontend opens a login panel if no user is logged in`() {
        // GIVEN no user is logged in.
        mutableAccount.value = null

        // WHEN the frontend opens a task panel.
        sut.openTaskPanels()

        // THEN the frontend opens a login panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountLoginPanel>().size)
    }

    @Test
    fun `frontend opens an info panel if the user is logged in`() {
        // GIVEN the user is logged in.
        mutableAccount.value = TestData.testAccount

        // WHEN the frontend opens a task panel.
        sut.openTaskPanels()

        // THEN the frontend opens an info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    @Test
    fun `frontend goes from login panel to info panel after login`() {
        // GIVEN no user is logged in.
        // AND the frontend opens a login panel.
        mutableAccount.value = null
        sut.openTaskPanels()

        // WHEN the user is logged in.
        mutableAccount.value = TestData.testAccount

        // THEN the frontend opens an info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    @Test
    fun `frontend goes from info panel to login panel after logout`() {
        // GIVEN the user is logged in.
        // AND the frontend opens an info panel.
        mutableAccount.value = TestData.testAccount
        sut.openTaskPanels()

        // WHEN the user is logged out
        mutableAccount.value = null

        // THEN the frontend opens an info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountLoginPanel>().size)
    }

    @Test
    fun `frontend does not re-open info panel if an user has changed`() {
        // GIVEN the user is logged in.
        // AND the frontend has an info panel opened.
        mutableAccount.value = TestData.testAccount
        sut.openTaskPanels()

        // WHEN another user is logged in.
        mutableAccount.value = TestData.anotherTestAccount

        // THEN the frontend still has an info panel opened.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }
}
