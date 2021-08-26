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

import org.gradle.api.JavaVersion

object Versions {
    // Gradle and plugins
    const val ARTIFACTORY_PLUGIN = "4.24.4"
    const val KOTLIN = "1.4.31"
    const val ANDROID_PLUGIN = "4.2.1"
    const val JVM = "1.8"
    val JAVA_COMPATIBILITY = JavaVersion.VERSION_1_8
    const val NAVTEST_PLUGIN = "10.0.3"
    const val NAVUI_EMULATORS_PLUGIN = "1.3.1"

    // Android
    const val ANDROIDX_ANNOTATION = "1.1.0"
    const val ANDROIDX_KTX = "1.3.2"
    const val ANDROIDX_LIFECYCLE = "2.3.1"

    const val BUILD_TOOLS = "30.0.2"

    const val COMPILE_SDK = 30
    const val MIN_SDK = 27
    const val TARGET_SDK = 27

    const val TOMTOM_ANDROID_TOOLS = "0.0.157"

    // Automotive
    const val ANDROID_CAR = "0.0.12"

    // Kotlin
    const val KOTLINX_SERIALIZATION = "1.1.0"

    // IVI

    // Communications Sdk
    const val IVI_COMMUNICATIONS_SDK = "0.1.51"

    // IndiGO platform version is updated by the `generateIndigoLibrariesVersionFile` gradle task.
    const val INDIGO_PLATFORM = "1.0.2255"
}
