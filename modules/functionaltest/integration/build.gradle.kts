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
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    id("com.tomtom.ivi.platform.framework.config.activity-test")
}

apply(from = rootProject.file("iviservicehosts.gradle.kts"))

val accountServiceHosts: List<IviServiceHostConfig> by project.extra

apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))

val accountFrontend: FrontendConfig by project.extra
val accountMenuItem: MenuItemConfig by project.extra

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                useDefaults()
                frontends {
                    add(accountFrontend)
                }
                menuItems {
                    addLast(accountMenuItem to accountFrontend)
                }
            }
        }
        services {
            addHosts(accountServiceHosts)
        }
    }
}

dependencies {
    androidTestImplementation(project(":serviceapis_account"))
    androidTestImplementation(project(":serviceapis_accountsettings"))
    androidTestImplementation(project(":services_customconnection"))
    androidTestImplementation(Libraries.TomTom.Indigo.TOOLS_TESTING_FRONTEND)
}
