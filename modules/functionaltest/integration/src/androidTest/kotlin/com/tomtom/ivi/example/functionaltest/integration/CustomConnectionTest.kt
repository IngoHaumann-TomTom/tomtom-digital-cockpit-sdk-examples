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

package com.tomtom.ivi.example.functionaltest.integration

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.telecom.TelecomManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tomtom.ivi.example.service.customconnection.CustomConnectionFacade
import com.tomtom.ivi.platform.tools.api.testing.functional.util.waitForNonUi
import com.tomtom.tools.android.testing.functional.TtFunctionalTestCase
import com.tomtom.tools.android.testing.functional.util.withIdleMainThread
import com.tomtom.tools.android.testing.functional.util.withMainThread
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CustomConnectionTest : TtFunctionalTestCase() {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private val customConnectionFacade = CustomConnectionFacade(instrumentation.targetContext)

    private val application = instrumentation.targetContext.applicationContext as Application

    private lateinit var defaultDialer: String

    @SuppressLint("VisibleForTests")
    @Before
    fun registerDebugPhoneAccount() {
        with(instrumentation) {
            defaultDialer = setDefaultDialerForAutomationTesting(this)
            customConnectionFacade.registerCustomPhoneAccount()
        }
    }

    @SuppressLint("VisibleForTests")
    @After
    fun unregisterDebugPhoneAccount() {
        with(instrumentation) {
            withIdleMainThread {
                runBlocking { customConnectionFacade.unregisterCustomPhoneAccount() }
            }
            setDefaultDialerForAutomationTesting(this, defaultDialer)
        }
    }

    @Test
    fun incomingCallIsCreatedProperly() {
        // GIVEN there are no ongoing calls
        assertFalse(customConnectionFacade.isInCall())

        // WHEN an incoming call is done
        // API classes that are using LiveData should be called from the main thread
        withMainThread {
            customConnectionFacade.createIncomingCall(phoneNumber = PHONE_NUMBER)
        }

        // AND the CustomConnectionService is ready
        waitForConnectionServiceToBeReady()

        // THEN there is an ongoing call
        assertTrue(customConnectionFacade.isInCall())
    }

    @Test
    fun outgoingCallIsCreatedProperly() {
        // GIVEN there are no ongoing calls
        assertFalse(customConnectionFacade.isInCall())

        // WHEN an outgoing call is done
        // API classes that are using LiveData should be called from the main thread
        withMainThread {
            customConnectionFacade.createOutgoingCall(phoneNumber = PHONE_NUMBER)
        }
        // AND the CustomConnectionService is ready,
        waitForConnectionServiceToBeReady()

        // THEN there is an ongoing call
        assertTrue(customConnectionFacade.isInCall())
    }

    private fun setDefaultDialerForAutomationTesting(
        instrumentation: Instrumentation,
        packageName: String = instrumentation.targetContext.applicationContext.packageName
    ): String {
        val telecomManager = instrumentation
            .targetContext.getSystemService(Activity.TELECOM_SERVICE) as TelecomManager
        val defaultDialer = telecomManager.defaultDialerPackage
        if (!isDefaultDialer()) {
            instrumentation.uiAutomation
                .executeShellCommand("telecom set-default-dialer $packageName")
                .use {
                    BufferedReader(InputStreamReader(FileInputStream(it.fileDescriptor))).readLine()
                }
            check(telecomManager.defaultDialerPackage == packageName)
        }
        return defaultDialer!!
    }

    private fun isDefaultDialer(): Boolean {
        val telecomManager =
            application.getSystemService(Activity.TELECOM_SERVICE) as TelecomManager?
        requireNotNull(telecomManager)
        return application.packageName == telecomManager.defaultDialerPackage
    }

    private fun waitForConnectionServiceToBeReady() =
        assertTrue(
            waitForNonUi(CONNECTION_SERVICE_TIMEOUT_MS) {
                customConnectionFacade.isCustomConnectionServiceReady()
            }
        )

    companion object {
        const val PHONE_NUMBER = "+12345678"
        const val CONNECTION_SERVICE_TIMEOUT_MS = 10_000L
    }
}
