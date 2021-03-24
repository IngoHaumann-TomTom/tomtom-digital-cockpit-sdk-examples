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

import com.tomtom.ivi.buildsrc.environment.Libraries
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

ivi {
    application {
        enabled = true
    }
}

android {
    buildFeatures {
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.tomtom.ivi.example"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(Libraries.TomTom.Automotive.ANDROID_CAR)

    implementation(Libraries.TomTom.Indigo.API_DEFAULTS_ACTIVITY)
    implementation(Libraries.TomTom.Indigo.API_DEFAULTS_APPLICATION)
    implementation(Libraries.TomTom.Indigo.CORE_COMMON_THEME)
    implementation(Libraries.TomTom.Indigo.CORE_DEBUG_PERMISSIONS)

    implementation(project(":frontends_account"))
    implementation(project(":serviceapis_account"))
    runtimeOnly(project(":services_account"))
}
