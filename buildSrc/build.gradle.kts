/*
 * Copyright © 2021 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

plugins {
    `kotlin-dsl`
}

dependencies {
    // Enforce the same version for all Kotlin libraries.
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("android-extensions", libraries.versions.kotlin.get()))
    implementation(libraries.gradlePluginKotlin)
    implementation(libraries.gradlePluginKotlinSerialization)
    implementation(libraries.gradleAndroidToolsBuildGradle)
    implementation(libraries.gradleAndroidToolsSdklib)
    implementation(libraries.gradlePluginApiDefaultsCore)
    implementation(libraries.gradlePluginApiDefaultsNavkit2)
    implementation(libraries.gradlePluginApiFrameworksConfig)
    implementation(libraries.gradlePluginApiFrameworksFrontend)
    implementation(libraries.gradlePluginApiToolsNavkit2Test)
    implementation(libraries.gradlePluginApiToolsSigningConfig)
    implementation(libraries.gradlePluginApiToolsEmulators)
    implementation(libraries.gradlePluginApiToolsVersionIvi)
    implementation(libraries.gradlePluginArtifactory)
    implementation(libraries.gradlePluginNavtestAndroid)
    implementation(libraries.gradlePluginNavtestCore)
    implementation(libraries.gradlePluginNavuiEmulators)
}
