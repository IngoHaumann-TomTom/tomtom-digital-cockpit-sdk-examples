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

import com.tomtom.ivi.buildsrc.environment.Libraries
import com.tomtom.ivi.buildsrc.extensions.androidTest

plugins {
    id("com.tomtom.ivi.platform.tools.navkit2-functionaltest")
}

androidTest {
    targetProjectPath = ":products_exampleapp"
}

android {
    navTest.android {
        androidTest {
            enabled.set(true)
            testTags.add("e2e")
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
