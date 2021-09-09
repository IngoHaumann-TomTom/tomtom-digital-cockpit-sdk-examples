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

buildscript {
    apply("repositories.gradle.kts")
}
apply("repositories.gradle.kts")
apply("versionsMap.gradle.kts")

plugins {
    `kotlin-dsl`
}

dependencies {
    val versions: Map<String, String> by rootProject.extra

    implementation(kotlin("gradle-plugin", versions["KOTLIN"]))
    // Enforces the same version for Kotlin libraries.
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("android-extensions", versions["KOTLIN"]))
    implementation(kotlin("serialization", versions["KOTLIN"]))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:${versions["ARTIFACTORY_PLUGIN"]}")
    implementation("com.android.tools.build:gradle:${versions["ANDROID_PLUGIN"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_defaults_core:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_defaults_navkit2:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_framework_config:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_framework_frontend:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_tools_navkit2test:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_tools_signingconfig:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_tools_emulators:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.ivi.platform.gradle:api_tools_version:${versions["INDIGO_PLATFORM"]}")
    implementation("com.tomtom.navtest:navtest-core:${versions["NAVTEST_PLUGIN"]}")
    implementation("com.tomtom.navui:emulators-plugin:${versions["NAVUI_EMULATORS_PLUGIN"]}")
}
