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

repositories {
    // Local artifact cache
    mavenLocal()

    // PU IVI repo for external parties
    if (project.hasProperty("nexusUsername")) {
        maven("https://repo.tomtom.com/repository/ivi") {
            credentials {
                username = properties["nexusUsername"].toString()
                password = properties["nexusPassword"].toString()
            }
        }
    } else {
        // PU IVI repo for internal dependencies
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/ivi-maven")
    }

    // Repo for shared Android Tools like, UI Controls, resource resolutions,
    // viewcomparison test setup, animations...
    maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/nav-maven-release")

    // External repositories
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")

    // PU NAV repo for NavTest and Gradle plugins from NAV
    maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/navapp-releases")

    // PU NAV repo for NavKit2
    maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/navkit2-maven-release-local")

    // PU LNS repo for the Connectivity Agent
    maven("https://maven.tomtom.com:8443/nexus/content/repositories/releases/")
}

val artifactoryRepo by rootProject.extra("https://artifactory.navkit-pipeline.tt3.com/artifactory")