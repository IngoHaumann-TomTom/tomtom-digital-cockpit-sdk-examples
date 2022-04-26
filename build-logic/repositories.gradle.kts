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

 /**
  * Note:
  * This configuration file is intended for TomTom developers that have access to the internal
  * Artifactory repositories.
  * By default, this file is not included in the SDK packaging: it is replaced by CI scripting with
  * the `sdk-repositories.gradle.kts` file in this folder.
  * To compile the Example app as an external developer, follow the instructions in the
  * `Getting Started` guide of the Developer Portal.
  */
pluginManagement {
    repositories {
        // Local artifact cache.
        mavenLocal()

        // Artifactory cache for Maven Central, JCenter, etc.
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/maven-remotes")
        // Repo for shared Android Tools plugins.
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/nav-maven-release/")
    }
}

dependencyResolutionManagement {
    repositories {
        // Local artifact cache.
        mavenLocal()

        // Artifactory cache for Maven Central, JCenter, etc.
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/maven-remotes")

        // PU IVI repo for internal dependencies.
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/ivi-maven")
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/ivi-test")

        // Repo for shared Android Tools like, UI Controls, resource resolutions,
        // viewcomparison test setup, animations...
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/nav-maven-release")
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/nav-maven-dev")

        // PU NAV repo for NavTest and Gradle plugins from NAV.
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/navapp-releases")

        // PU NAV repo for NavKit2.
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/navkit2-maven-release-local")

        // PU LNS repo for the Connectivity Agent.
        maven("https://maven.tomtom.com:8443/nexus/content/repositories/releases/")

        // Analytics repo (used by NavApp components)
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/dataunit-maven-release")
    }
}
