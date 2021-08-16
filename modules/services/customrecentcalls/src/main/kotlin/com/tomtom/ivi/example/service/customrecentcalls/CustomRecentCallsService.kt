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

import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
import com.tomtom.ivi.core.common.contacts.PhoneNumber
import com.tomtom.ivi.core.common.contacts.PhoneNumberType
import com.tomtom.ivi.core.common.contacts.SynchronizationStatus
import com.tomtom.ivi.core.serviceapi.recentcalls.RecentCallsService
import com.tomtom.ivi.core.serviceapi.recentcalls.RecentCallsServiceBase
import java.time.Duration
import java.time.Instant

/**
 * A custom recent calls service that contains two recent calls.
 */
class CustomRecentCallsService(iviServiceHostContext: IviServiceHostContext) :
    RecentCallsServiceBase(iviServiceHostContext) {

    // The source of recent calls.
    private val recentCallsSource = listOf(
        RecentCallsService.RecentCall(
            type = RecentCallsService.CallType.INCOMING,
            displayName = "John Smith",
            phoneNumber = PhoneNumber("+281111111", PhoneNumberType.Mobile),
            duration = Duration.ofSeconds(60),
            creationTime = Instant.now()
        ),
        RecentCallsService.RecentCall(
            type = RecentCallsService.CallType.OUTGOING,
            displayName = "Kelly Goodwin",
            phoneNumber = PhoneNumber("+2822222222", PhoneNumberType.Work),
            duration = Duration.ZERO,
            creationTime = Instant.now().minus(Duration.ofHours(1))
        ),
    )

    override fun onCreate() {
        super.onCreate()
        // Initialize the synchronization status.
        synchronizationStatus = SynchronizationStatus.NO_CONNECTED_DEVICES
        // Initialize the recentCalls property with an empty list.
        recentCalls = listOf()
        // Set the service to ready. Now clients can call the service's APIs.
        serviceReady = true
        // The source of recent calls is ready and synchronization starts.
        synchronizationStatus = SynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS
        // Update the recent calls property with some recent calls from the source.
        recentCalls = recentCallsSource
    }

    override fun onDestroy() {
        // Put cleanup code here, if necessary.
        super.onDestroy()
    }
}