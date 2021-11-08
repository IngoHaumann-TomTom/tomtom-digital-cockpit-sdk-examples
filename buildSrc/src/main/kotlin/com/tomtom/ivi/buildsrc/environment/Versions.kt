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

package com.tomtom.ivi.buildsrc.environment

import org.gradle.api.JavaVersion

object Versions {
    // Gradle and plugins
    const val ARTIFACTORY_PLUGIN = "4.24.4"
    const val KOTLIN = "1.5.21"
    const val ANDROID_PLUGIN = "7.0.2"
    const val ANDROID_SDK_COMMON = "30.0.0"
    const val JVM = "1.8"
    val JAVA_COMPATIBILITY = JavaVersion.VERSION_1_8
    const val NAVTEST_PLUGIN = "11.0.0"
    const val NAVUI_EMULATORS_PLUGIN = "2.0.0"

    // Android
    const val ANDROIDX_ACTIVITY = "1.3.1"
    const val ANDROIDX_ANNOTATION = "1.1.0"
    const val ANDROIDX_KTX = "1.3.2"
    const val ANDROIDX_LIFECYCLE = "2.3.1"

    const val BUILD_TOOLS = "30.0.2"

    const val COMPILE_SDK = 30
    const val MIN_SDK = 27
    const val TARGET_SDK = 27

    const val TOMTOM_ANDROID_TOOLS = "0.0.240"

    // Automotive
    const val ANDROID_CAR = "0.0.12"

    // Kotlin
    const val KOTLINX_SERIALIZATION = "1.2.1"

    // IVI

    // Communications Sdk
    const val IVI_COMMUNICATIONS_SDK = "0.1.72"

    // IndiGO platform version is updated by the `generateIndigoLibrariesVersionFile` gradle task.
    const val INDIGO_PLATFORM = "1.0.2692"
}
