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
import com.tomtom.ivi.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.gradle.api.plugin.defaultsplatform.contactsServiceHost
import com.tomtom.ivi.gradle.api.plugin.defaultsplatform.recentCallsServiceHost
import com.tomtom.ivi.gradle.api.plugin.defaultsplatform.userProfileFrontend
import com.tomtom.ivi.gradle.api.plugin.defaultsplatform.userProfileMenuItem
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

plugins {
    id("com.tomtom.ivi.defaults.core")
    id("com.tomtom.ivi.defaults.navkit2")
}

apply(from = rootProject.file("iviservicehosts.gradle.kts"))

val accountServiceHosts: List<IviServiceHostConfig> by project.extra
val customContactsServiceHost: IviServiceHostConfig by project.extra
val customRecentCallsServiceHost: IviServiceHostConfig by project.extra

apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))

val accountFrontend: FrontendConfig by project.extra
val accountMenuItem: MenuItemConfig by project.extra
val hierarchyFrontend: FrontendConfig by project.extra
val hierarchyMenuItem: MenuItemConfig by project.extra

/**
 * Configures the main application.
 */
ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                useDefaults()
                frontends {
                    replace(userProfileFrontend, accountFrontend)
                    add(hierarchyFrontend)
                }
                menuItems {
                    replace(userProfileMenuItem, accountMenuItem to accountFrontend)
                    addLast(hierarchyMenuItem to hierarchyFrontend)
                }
            }
        }
        services {
            // Register the account and account settings services in the application.
            addHosts(accountServiceHosts)

            // Replace the default contact service with the custom contacts service.
            removeHost(contactsServiceHost)
            addHost(customContactsServiceHost)

            // Replace the default recent calls service host with the custom recent calls service
            // host.
            removeHost(recentCallsServiceHost)
            addHost(customRecentCallsServiceHost)
        }
        runtime {
            deployments {
                create(RuntimeDeploymentIdentifier.globalRuntime) {
                    useDefaults()
                    // Deploys the account and account settings services in the same process.
                    deployServiceHosts(inList(accountServiceHosts))
                        .withProcessName("account")
                }
            }
        }
    }
}

android {
    defaultConfig {
        applicationId = "com.tomtom.ivi.example.product.exampleapp"
    }

    buildFeatures {
        dataBinding = true
    }

    signingConfigs.maybeCreate("release")

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(project(":frontends_account"))
    implementation(Libraries.TomTom.Automotive.ANDROID_CAR)
    implementation(Libraries.TomTom.Indigo.API_DEFAULTS_ACTIVITY)
    implementation(Libraries.TomTom.Indigo.API_DEFAULTS_APPLICATION)
    implementation(Libraries.TomTom.Indigo.CORE_COMMON_THEME)
    implementation(Libraries.TomTom.Indigo.CORE_CONFIG_NAVKIT2)
    implementation(Libraries.TomTom.Indigo.CORE_DEBUG_PERMISSIONS)
}
