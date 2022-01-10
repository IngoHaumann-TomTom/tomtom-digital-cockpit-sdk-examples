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

import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendExtensionConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.defaults.config.contactsServiceHost
import com.tomtom.ivi.platform.gradle.api.defaults.config.recentCallsServiceHost
import com.tomtom.ivi.platform.gradle.api.defaults.config.userProfileFrontend
import com.tomtom.ivi.platform.gradle.api.defaults.config.userProfileMenuItem
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    id("com.tomtom.ivi.platform.defaults.core")
    id("com.tomtom.ivi.platform.defaults.navkit2")
}

apply(from = rootProject.file("iviservicehosts.gradle.kts"))

val accountServiceHosts: List<IviServiceHostConfig> by project.extra
val customCompanionExampleServiceHost: IviServiceHostConfig by project.extra
val customContactsServiceHost: IviServiceHostConfig by project.extra
val customRecentCallsServiceHost: IviServiceHostConfig by project.extra

apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))

val accountFrontend: FrontendConfig by project.extra
val accountMenuItem: MenuItemConfig by project.extra
val hierarchyFrontend: FrontendConfig by project.extra
val hierarchyMenuItem: MenuItemConfig by project.extra
val debugFrontend: FrontendConfig by project.extra
val activityViewDebugTabFrontendExtension: FrontendExtensionConfig by project.extra

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
                    configureIfPresent(debugFrontend) {
                        addExtension(activityViewDebugTabFrontendExtension)
                    }
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

            addHost(customCompanionExampleServiceHost)
        }
        runtime {
            globalDeployments {
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
    implementation(project(":debugtabs_activityview"))
    implementation(project(":frontends_account"))
    implementation(libraries.tomtomAutomotiveAndroidCar)
    implementation(libraries.indigoPlatformFrameworkApiProductDebugPermissions)
    implementation(libraries.indigoPlatformFrameworkApiProductDefaultActivity)
    implementation(libraries.indigoPlatformFrameworkApiProductDefaultApplication)
    implementation(libraries.indigoPlatformNavkit2ApiCommonConfiguration)
    implementation(libraries.indigoPlatformThemingApiCommonAttributes)
}
