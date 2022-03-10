/*
 * Copyright © 2021. Change this to your own official copyright statement.
 */

import com.tomtom.ivi.buildsrc.environment.ProjectAbis
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    id("com.tomtom.ivi.product.defaults.core")
    id("com.tomtom.ivi.appsuite.navkit2.defaults.config")
}

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                useDefaults()
            }
        }
        runtime {
            globalDeployments {
                create(RuntimeDeploymentIdentifier.globalRuntime) {
                    useDefaults()
                }
            }
        }
    }
}

android {
    defaultConfig {
        applicationId = "com.example.ivi.template.app"
    }

    buildFeatures {
        dataBinding = true
    }

    signingConfigs.maybeCreate("release")
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Split the output into multiple APKs based on their ABI.
    splits.abi {
        isEnable = true
        reset()
        include(*ProjectAbis.enabledAbis)
    }
}

dependencies {
    implementation(indigoDependencies.tomtomAutomotiveAndroidCar)
    implementation(libraries.indigoPlatformThemingApiCommonAttributes)
    implementation(libraries.indigoPlatformNavkit2ApiCommonConfiguration)
    implementation(libraries.indigoPlatformFrameworkApiProductDebugPermissions)
    implementation(libraries.indigoPlatformFrameworkApiProductDefaultActivity)
    implementation(libraries.indigoPlatformFrameworkApiProductDefaultApplication)
}
