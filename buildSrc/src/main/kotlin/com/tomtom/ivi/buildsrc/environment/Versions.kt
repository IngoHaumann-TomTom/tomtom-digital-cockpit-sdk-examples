/*
 * Copyright (c) 2020 - 2020 TomTom N.V. All rights reserved.
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
    const val KOTLIN = "1.4.10"
    const val ANDROID_PLUGIN = "4.1.0"
    const val JVM = "1.8"
    val JAVA_COMPATIBILITY = JavaVersion.VERSION_1_8

    // Android
    const val ANDROIDX_ANNOTATION = "1.1.0"
    const val ANDROIDX_KTX = "1.2.0"

    const val BUILD_TOOLS = "29.0.3"

    const val COMPILE_SDK = 29
    const val MIN_SDK = 27
    const val TARGET_SDK = 27

    // Automotive
    const val ANDROID_CAR = "0.0.11"

    // IVI
    // TODO(IVI-2177): Use the latest version.
    const val INDIGO_PLATFORM = "1.0.866"
}
