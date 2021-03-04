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

package com.tomtom.ivi.example.frontent.account

import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.framework.frontend.panels.filterPanels
import com.tomtom.ivi.example.frontend.account.AccountFrontend
import com.tomtom.ivi.example.frontend.account.info.AccountInfoPanel
import com.tomtom.ivi.example.frontend.account.login.AccountLoginPanel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.tools.testing.mock.niceMockk
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import io.mockk.every
import org.junit.Test

class AccountFrontendTest : IviTestCase() {
    private val mutableUsername = MutableLiveData<String?>(null).also {
        // Service mock must be configured before a frontend is created.
        mockkService(AccountService.Companion::createApi) {
            every { serviceAvailable } returns MutableLiveData(true)
            every { username } returns it
        }
    }

    private val sut = AccountFrontend(niceMockk()).apply { onCreate() }

    @Test
    fun `frontend does not open a panel after an user has logged in`() {
        // GIVEN no user is logged in.
        mutableUsername.value = null

        // WHEN an user has logged in.
        mutableUsername.value = USERNAME

        // THEN the frontend does not open a panel.
        assertTrue(sut.panels.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `frontend does not open a panel after an user has logged out`() {
        // GIVEN an user is logged in.
        mutableUsername.value = USERNAME

        // WHEN the user has logged out
        mutableUsername.value = null

        // THEN the frontend does not open a panel.
        assertTrue(sut.panels.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `frontend opens a login panel if no user is logged in`() {
        // GIVEN no user is logged in.
        mutableUsername.value = null

        // WHEN the frontend opens a task panel.
        sut.openTaskPanels()

        // THEN the frontend opens a login panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountLoginPanel>().size)
    }

    @Test
    fun `frontend opens an info panel if the user is logged in`() {
        // GIVEN the user is logged in.
        mutableUsername.value = USERNAME

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
        mutableUsername.value = null
        sut.openTaskPanels()

        // WHEN the user is logged in.
        mutableUsername.value = USERNAME

        // THEN the frontend opens an info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    @Test
    fun `frontend goes from info panel to login panel after logout`() {
        // GIVEN the user is logged in.
        // AND the frontend opens an info panel.
        mutableUsername.value = USERNAME
        sut.openTaskPanels()

        // WHEN the user is logged out
        mutableUsername.value = null

        // THEN the frontend opens an info panel.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountLoginPanel>().size)
    }

    @Test
    fun `frontend does not re-open info panel if an user has changed`() {
        // GIVEN the user is logged in.
        // AND the frontend has an info panel opened.
        mutableUsername.value = USERNAME
        sut.openTaskPanels()

        // WHEN another user is logged in.
        mutableUsername.value = "anotherTestUser"

        // THEN the frontend still has an info panel opened.
        assertEquals(1, sut.panels.getOrAwaitValue().size)
        assertEquals(1, sut.panels.getOrAwaitValue().filterPanels<AccountInfoPanel>().size)
    }

    companion object {
        private const val USERNAME = "testUser"
    }
}