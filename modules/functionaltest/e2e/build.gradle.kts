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
import com.tomtom.ivi.buildsrc.extensions.androidTest

plugins {
    id("com.tomtom.ivi.tools.allow-mock-location")
    id("com.tomtom.ivi.tools.webservice-mock")
}

androidTest {
    targetProjectPath = ":products_exampleapp"

    configure<com.tomtom.navtest.NavTestAndroidProjectExtension> {
        androidTest {
            enabled = true
            testTags += "e2e"
        }
    }
}

dependencies {
    implementation(project(":frontends_account"))
    implementation(Libraries.TomTom.Indigo.TOOLS_TESTING_FUNCTIONAL)
    implementation(Libraries.TomTom.Indigo.TOOLS_TESTING_MAINMENUFRONTEND)
    implementation(Libraries.TomTom.Indigo.TOOLS_TESTING_NAVIGATIONFUNCTIONAL)
    implementation(Libraries.TomTom.Indigo.TOOLS_TESTING_SYSTEMUI)
}
