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
import com.tomtom.ivi.buildsrc.config.services.customContactsServiceHost
import com.tomtom.ivi.buildsrc.environment.Libraries
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServicesConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier.Companion.globalRuntime
import com.tomtom.ivi.gradle.api.plugin.defaultsplatform.defaultPlatformServiceHosts
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

/**
 * Configures the main application.
 */
ivi {
    application {
        enabled = true
        services {
            // Register the account and account settings services in the application.
            addHosts(accountServiceHosts)

            // Register the custom contacts service.
            replaceDefaultHost(customContactsServiceHost)
        }
        runtime {
            deployments {
                create(globalRuntime) {
                    useDefaults()
                    // Deploys the account and account settings services in the same process.
                    deployServiceHosts(inList(accountServiceHosts))
                        .withProcessName("accountservicehost")
                }
            }
        }
    }
}

android {
    buildFeatures {
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.tomtom.ivi.example.product.exampleapp"
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
    implementation(project(":frontends_account"))
    implementation(Libraries.TomTom.Automotive.ANDROID_CAR)
    implementation(Libraries.TomTom.Indigo.API_DEFAULTS_ACTIVITY)
    implementation(Libraries.TomTom.Indigo.API_DEFAULTS_APPLICATION)
    implementation(Libraries.TomTom.Indigo.CORE_COMMON_THEME)
    implementation(Libraries.TomTom.Indigo.CORE_DEBUG_PERMISSIONS)

}

/**
 * Replace the default service host by the new service host when at least one interface is common in
 * [IviServiceHostConfig.interfaces].
 * If no common interfaces found, the new host is added and no default host is removed.
 */
fun IviServicesConfig.replaceDefaultHost(exampleServiceHost : IviServiceHostConfig) {
    defaultPlatformServiceHosts.firstOrNull {
        it.interfaces.intersect(exampleServiceHost.interfaces).isNotEmpty()
    }?.let { iviDefaultServiceHost ->
        removeHost(iviDefaultServiceHost)
    }
    addHost(exampleServiceHost)
}
