package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.tools.testing.mock.niceMockk
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class StockAccountServiceTest : IviTestCase() {

    private val sut = StockAccountService(niceMockk())

    @Test
    fun `no user is logged in by default`() {
        assertNull(sut.username)
    }

    @Test
    fun `login failed if username or password are incorrect`() = runBlocking {
        assertFalse(sut.logIn(USERNAME, ""))
        assertNull(sut.username)

        assertFalse(sut.logIn("", PASSWORD))
        assertNull(sut.username)
    }

    @Test
    fun `username is set if user has logged in`() = runBlocking {
        assertTrue(sut.logIn(USERNAME, PASSWORD))
        assertEquals(USERNAME, sut.username)
    }

    @Test
    fun `username is reset if user has logged out`() = runBlocking {
        // GIVEN
        sut.logIn(USERNAME, PASSWORD)

        // WHEN
        sut.logOut()

        // THEN
        assertNull(sut.username)
    }

    @Test
    fun `logging out with no user logged in is no-op`() = runBlocking {
        // GIVEN
        // WHEN
        sut.logOut()

        // THEN
        assertNull(sut.username)
    }

    @Test
    fun `logging in with the user logged in updates the username`() = runBlocking {
        // GIVEN
        sut.logIn(USERNAME, PASSWORD)

        val anotherUsername = "anotherTestUser"

        // WHEN
        val result = sut.logIn(anotherUsername, PASSWORD)

        // THEN
        assertTrue(result)
        assertEquals(anotherUsername, sut.username)
    }

    companion object {
        private const val USERNAME = "testUser"
        private const val PASSWORD = "testPassword"
    }
}