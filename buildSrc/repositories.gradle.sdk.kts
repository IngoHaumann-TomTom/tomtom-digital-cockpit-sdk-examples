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

repositories {
    // Local artifact cache
    mavenLocal()

    // TomTom PU IVI repo for the IndiGO product
    maven("https://repo.tomtom.com/repository/ivi") {
        credentials {
            username = properties["nexusUsername"].toString()
            password = properties["nexusPassword"].toString()
        }
    }

    // External repositories
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")

    // TomTom PU LNS repo for the Connectivity Agent
    maven("https://maven.tomtom.com:8443/nexus/content/repositories/releases/")
}

// TODO(IVI-4701): Remove Artifactory reference.
val artifactoryRepo by rootProject.extra("https://artifactory.navkit-pipeline.tt3.com/artifactory")
