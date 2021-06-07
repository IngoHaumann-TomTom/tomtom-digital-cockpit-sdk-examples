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
        assertEquals(2, sut.recentCalls.size)
        assertEquals("John Smith", sut.recentCalls[0].displayName)
        assertEquals("Kelly Goodwin", sut.recentCalls[1].displayName)
        assertEquals(SynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS, sut.synchronizationStatus)
    }
}