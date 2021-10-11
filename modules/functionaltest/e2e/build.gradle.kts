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
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    id("com.tomtom.ivi.platform.tools.navkit2-functionaltest")
}

ivi {
    /**
     * TODO(IVI-2177): The public interface of the E2E testing classes are not yet stable.
     *   Therefore, this annotation is needed in this and customer projects. Before the mentioned
     *   ticket is closed, check if this is still needed, or change the mentioned ticket reference
     *   to the relevant ticket which relates to making the E2E testing classes stable.
     */
    optInToExperimentalApis = true
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
