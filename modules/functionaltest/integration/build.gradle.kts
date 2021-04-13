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

import com.tomtom.ivi.buildsrc.config.services.accountServiceHosts
import com.tomtom.ivi.buildsrc.environment.Libraries
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

plugins {
    id("com.tomtom.ivi.platform.activity-test")
}

ivi {
    application {
        enabled = true
        services {
            addHosts(accountServiceHosts)
        }
    }
}

dependencies {
    // Implementation dependency to get access to the project resources.
    implementation(project(":frontends_account"))

    androidTestImplementation(project(":serviceapis_account"))
    androidTestImplementation(project(":serviceapis_accountsettings"))
    androidTestImplementation(Libraries.TomTom.Indigo.TOOLS_TESTING_FRONTEND)
}
