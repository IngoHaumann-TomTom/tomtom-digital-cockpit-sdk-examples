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

package com.tomtom.ivi.buildsrc.environment

import com.android.sdklib.devices.Abi

/**
 * Contains the names of APK ABIs to build and publish.
 */
object ProjectAbis {

    /**
     * These ABIs need to match those officially supported by NavKit 2.
     */
    private val supportedAbis = listOf(
        Abi.ARM64_V8A,
        Abi.ARMEABI_V7A,
        Abi.X86_64,
        Abi.X86
    )

    val enabledAbis =
        listOf(
            Abi.ARM64_V8A,
            Abi.ARMEABI_V7A,
            Abi.X86_64
        )
            .also { require(supportedAbis.containsAll(it)) }
            .map { it.toString() }
            .toTypedArray()
}
