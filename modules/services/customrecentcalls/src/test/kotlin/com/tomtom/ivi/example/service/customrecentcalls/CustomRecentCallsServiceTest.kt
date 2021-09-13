/*
 * Copyright © 2020 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, then you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.example.service.customrecentcalls

import com.tomtom.ivi.core.common.contacts.SynchronizationStatus
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import com.tomtom.tools.android.testing.mock.niceMockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CustomRecentCallsServiceTest : IviTestCase() {

    private val sut = CustomRecentCallsService(niceMockk())

    @Before
    fun createSut() {
        sut.onCreate()
    }

    @Test
    fun initialization() {
        // GIVEN-WHEN-THEN
        assertEquals(2, sut.recentCallsDescending.size)
        assertEquals("John Smith", sut.recentCallsDescending[0].displayName)
        assertEquals("Kelly Goodwin", sut.recentCallsDescending[1].displayName)
        assertEquals(SynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS, sut.synchronizationStatus)
    }
}