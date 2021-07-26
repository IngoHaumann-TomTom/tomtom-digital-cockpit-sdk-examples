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

package com.tomtom.ivi.example.service.customconnection

import android.content.ComponentName
import android.os.Bundle
import android.telecom.Connection.STATE_DIALING
import android.telecom.Connection.STATE_RINGING
import android.telecom.ConnectionRequest
import android.telecom.PhoneAccountHandle
import com.tomtom.ivi.core.common.telecom.toPhoneUri
import com.tomtom.ivi.tools.testing.unit.IviTestCase
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CustomConnectionServiceTest : IviTestCase() {

    private val sut by lazy { CustomConnectionService() }

    @Before
    fun setUpDebugConnectionService() {
        sut.onDestroy()
    }

    @After
    fun tearDownDebugConnectionService() {
        sut.onDestroy()
    }

    @Test
    fun `on create`() {
        // GIVEN
        mockkObject(CustomConnectionServiceHolder)

        // WHEN
        sut.onCreate()

        // THEN
        verify {
            CustomConnectionServiceHolder.setCustomConnectionService(sut)
        }
    }

    @Test
    fun `on destroy`() {
        // GIVEN
        mockkObject(CustomConnectionServiceHolder)

        // WHEN
        sut.onDestroy()

        // THEN
        verify {
            CustomConnectionServiceHolder.setCustomConnectionService(null)
        }
    }

    @Test
    fun `on create incoming connection`() {
        // GIVEN
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(
                context,
                CustomConnectionService::class.qualifiedName!!
            ),
            CustomConnectionFacade.IVI_CUSTOM_PHONE_ACCOUNT_ID
        )
        val request = ConnectionRequest(phoneAccountHandle, PHONE_NUMBER.toPhoneUri(), Bundle.EMPTY)

        // WHEN
        val connection = sut.onCreateIncomingConnection(phoneAccountHandle, request)

        // THEN
        assertEquals(PHONE_NUMBER.toPhoneUri(), connection?.address)
        assertEquals(STATE_RINGING, connection?.state)
    }

    @Test
    fun `on create outgoing connection`() {
        // GIVEN
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(
                context,
                CustomConnectionService::class.qualifiedName!!
            ),
            CustomConnectionFacade.IVI_CUSTOM_PHONE_ACCOUNT_ID
        )
        val request = ConnectionRequest(phoneAccountHandle, PHONE_NUMBER.toPhoneUri(), Bundle.EMPTY)

        // WHEN
        val connection = sut.onCreateOutgoingConnection(phoneAccountHandle, request)

        // THEN
        assertEquals(PHONE_NUMBER.toPhoneUri(), connection?.address)
        assertEquals(STATE_DIALING, connection?.state)
    }

    companion object {
        private const val PHONE_NUMBER = "+123456789"
    }
}