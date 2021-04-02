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

import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StockAccountServiceTest : IviTestCase() {

    private val sut = StockAccountService(niceMockk())

    @Before
    fun before() {
        sut.onCreate()
    }

    @Test
    fun `no user is logged in by default`() {
        assertNull(sut.activeAccount)
        assertEquals(0, sut.accounts.size)
    }

    @Test
    fun `login failed if activeAccount or password are incorrect`() = runBlocking {
        assertFalse(sut.logIn(USERNAME, ""))
        assertNull(sut.activeAccount)
        assertEquals(0, sut.accounts.size)

        assertFalse(sut.logIn("", PASSWORD))
        assertNull(sut.activeAccount)
        assertEquals(0, sut.accounts.size)
    }

    @Test
    fun `activeAccount is set if user has logged in`() = runBlocking {
        assertTrue(sut.logIn(USERNAME, PASSWORD))
        assertEquals(USERNAME, sut.activeAccount?.username)
        assertEquals(1, sut.accounts.size)
    }

    @Test
    fun `activeAccount is reset if user has logged out`() = runBlocking {
        // GIVEN
        sut.logIn(USERNAME, PASSWORD)

        // THEN
        assertEquals(1, sut.accounts.size)

        // WHEN
        sut.logOut()

        // THEN
        assertNull(sut.activeAccount)
        assertEquals(1, sut.accounts.size)
    }

    @Test
    fun `logging out with no user logged in is no-op`() = runBlocking {
        // GIVEN
        // WHEN
        sut.logOut()

        // THEN
        assertNull(sut.activeAccount)
    }

    @Test
    fun `logging in with the user logged in updates the activeAccount`() = runBlocking {
        // GIVEN
        sut.logIn(USERNAME, PASSWORD)

        val anotherTestUser = "anotherTestUser"

        // WHEN
        val result = sut.logIn(anotherTestUser, PASSWORD)

        // THEN
        assertTrue(result)
        assertEquals(anotherTestUser, sut.activeAccount?.username)
        assertEquals(2, sut.accounts.size)
    }

    companion object {
        private const val USERNAME = "testUser"
        private const val PASSWORD = "testPassword"
    }
}