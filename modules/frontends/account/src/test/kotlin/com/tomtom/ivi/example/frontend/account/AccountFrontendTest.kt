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
import androidx.test.platform.app.InstrumentationRegistry
import com.tomtom.ivi.api.framework.frontend.panels.filterPanels
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.frontend.account.info.AccountInfoPanel
import com.tomtom.ivi.example.frontend.account.login.AccountLoginPanel
import com.tomtom.ivi.example.frontend.account.notification.AccountNotificationPanel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import io.mockk.every
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountFrontendTest : IviTestCase() {
    private val mutableAccount = MutableLiveData<Account?>(null).also {
        // Service mock must be configured before a frontend is created.
        mockkService(AccountService.Companion::createApi) {
            every { serviceAvailable } returns MutableLiveData(true)
            every { activeAccount } returns it
            every { accounts } returns MutableLiveData(TestData.accountsMap)
        }
    }

    private val sut = AccountFrontend(niceMockk()).apply { onCreate() }

    private fun getAccountNotificationPanels() =
        sut.panels
            .getOrAwaitValue()
            .filterPanels<AccountNotificationPanel>()

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
    fun `frontend opens an info panel if a user is logged in`() {
        // GIVEN an user is logged in.
        mutableAccount.value = TestData.testAccount
        // AND the notification is dismissed
        sut.panels.getOrAwaitValue().filterPanels<AccountNotificationPanel>().forEach {
            it.dismiss()
        }

        // WHEN the frontend opens a task panel.
        sut.openTaskPanels()

        // THEN the frontend opens a info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    @Test
    fun `frontend shows a notification after an user has logged in`() {
        // GIVEN no user is logged in.
        mutableAccount.value = null

        // WHEN an user has logged in.
        mutableAccount.value = TestData.testAccount

        // THEN the frontend shows only a single notification panel with correct username.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountNotificationPanel>().size)
        assertEquals(
            TestData.testAccount.username,
            getAccountNotificationPanels().first().userName.get(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        )
    }

    @Test
    fun `frontend does not open a panel or show a notification after an user has logged out`() {
        // GIVEN an user is logged in.
        mutableAccount.value = TestData.testAccount
        // AND the notification is dismissed
        sut.panels.getOrAwaitValue().filterPanels<AccountNotificationPanel>().forEach {
            it.dismiss()
        }

        // WHEN the user is logged out
        mutableAccount.value = null

        // THEN the frontend does not open a panel.
        assertTrue(sut.panels.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `frontend goes from info panel to login panel after logout`() {
        // GIVEN the user is logged in.
        mutableAccount.value = TestData.testAccount
        // AND the notification is dismissed
        sut.panels.getOrAwaitValue().filterPanels<AccountNotificationPanel>().forEach {
            it.dismiss()
        }
        // AND the frontend opens an info panel.
        sut.openTaskPanels()

        // WHEN the user is logged out
        mutableAccount.value = null

        // THEN the frontend opens an login panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountLoginPanel>().size)
    }

    @Test
    fun `frontend does not re-open info panel if an user has changed`() {
        // GIVEN the user is logged in.
        mutableAccount.value = TestData.testAccount
        // AND the notification is dismissed
        sut.panels.getOrAwaitValue().filterPanels<AccountNotificationPanel>().forEach {
            it.dismiss()
        }
        // AND the frontend has an info panel opened.
        sut.openTaskPanels()

        // WHEN another user is logged in.
        mutableAccount.value = TestData.anotherTestAccount

        // THEN a notification is shown with the new username.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, getAccountNotificationPanels().size)
        assertEquals(
            TestData.anotherTestAccount.username,
            getAccountNotificationPanels().first().userName.get(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        )
    }
}