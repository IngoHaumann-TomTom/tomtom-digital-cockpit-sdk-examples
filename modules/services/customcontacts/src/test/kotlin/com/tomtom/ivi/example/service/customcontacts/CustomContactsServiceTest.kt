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

package com.tomtom.ivi.example.service.customcontacts

import com.tomtom.ivi.core.common.contacts.ContactId
import com.tomtom.ivi.core.common.contacts.SynchronizationStatus
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CustomContactsServiceTest : IviTestCase() {

    private val sut = CustomContactsService(niceMockk())

    @Before
    fun createSut() {
        sut.onCreate()
    }

    @Test
    fun initialization() {
        // GIVEN-WHEN-THEN
        assertEquals(2, sut.contacts.size)
        assertEquals("John Smith", sut.contacts[ContactId("1")]?.displayName)
        assertEquals("Kelly Goodwin", sut.contacts[ContactId("2")]?.displayName)
        assertEquals(SynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS, sut.synchronizationStatus)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `only contact 1 has an image`() = runBlockingTest{
        // GIVEN-WHEN
        sut.onCreate()

        // THEN
        assertNotNull(sut.getImage(ContactId("1")))
        assertNull(sut.getImage(ContactId("2")))
    }
}