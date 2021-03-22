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

package com.tomtom.ivi.example.frontend.account

import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.framework.frontend.panels.filterPanels
import com.tomtom.ivi.example.frontend.account.info.AccountInfoPanel
import com.tomtom.ivi.example.frontend.account.login.AccountLoginPanel
import com.tomtom.ivi.example.serviceapi.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountId
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import io.mockk.every
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class AccountFrontendTest : IviTestCase() {
    private val mutableAccount = MutableLiveData<Account?>(null).also {
        // Service mock must be configured before a frontend is created.
        mockkService(AccountService.Companion::createApi) {
            every { serviceAvailable } returns MutableLiveData(true)
            every { activeAccount } returns it
            every { accounts } returns MutableLiveData(
                mapOf(ACCOUNT1ID to ACCOUNT1, ACCOUNT2ID to ACCOUNT2)
            )
        }
    }

    private val sut = AccountFrontend(niceMockk()).apply { onCreate() }

    @Test
    fun `frontend does not open a panel after an user has logged in`() {
        // GIVEN no user is logged in.
        mutableAccount.value = null

        // WHEN an user has logged in.
        mutableAccount.value = ACCOUNT1

        // THEN the frontend does not open a panel.
        assertTrue(sut.panels.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `frontend does not open a panel after an user has logged out`() {
        // GIVEN an user is logged in.
        mutableAccount.value = ACCOUNT1

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
        mutableAccount.value = ACCOUNT1

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
        mutableAccount.value = ACCOUNT1

        // THEN the frontend opens an info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    @Test
    fun `frontend goes from info panel to login panel after logout`() {
        // GIVEN the user is logged in.
        // AND the frontend opens an info panel.
        mutableAccount.value = ACCOUNT1
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
        mutableAccount.value = ACCOUNT1
        sut.openTaskPanels()

        // WHEN another user is logged in.
        mutableAccount.value = ACCOUNT2

        // THEN the frontend still has an info panel opened.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    companion object {
        private val ACCOUNT1ID = AccountId("ACCOUNT1")
        private val ACCOUNT1 = Account(ACCOUNT1ID, "USER1")
        private val ACCOUNT2ID = AccountId("ACCOUNT2")
        private val ACCOUNT2 = Account(ACCOUNT2ID, "USER2")
    }
}