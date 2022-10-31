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

val mainArtifactorySaasUsername: String? by extra
val mainArtifactorySaasToken: String? by extra

val useSaasArtifactory = (mainArtifactorySaasUsername != null) && (mainArtifactorySaasToken != null)

fun RepositoryHandler.tomtomArtifactory(repoName: String) {
    if (useSaasArtifactory) {
        maven("https://artifactory.tomtomgroup.com/artifactory/$repoName") {
            credentials {
                username = mainArtifactorySaasUsername
                password = mainArtifactorySaasToken
            }
        }
    } else {
        maven("https://artifactory.navkit-pipeline.tt3.com/artifactory/$repoName")
    }
}

// Doing it in this way (as opposed to `pluginManagement{repositories{...}}`)
// allows to use the `tomtomArtifactory` function defined above and avoid copypaste
// https://docs.gradle.org/current/userguide/upgrading_version_5.html#the_pluginmanagement_block_in_settings_scripts_is_now_isolated
pluginManagement.repositories {
    // Local artifact cache.
    mavenLocal()

    // Artifactory cache for Maven Central, JCenter, etc.
    tomtomArtifactory("maven-remotes")
    tomtomArtifactory("as-navapp-maven-release")
    // Repo for shared Android Tools plugins.
    tomtomArtifactory("nav-maven-release")
}

dependencyResolutionManagement {
    repositories {
        // Local artifact cache.
        mavenLocal()

        // Artifactory cache for Maven Central, JCenter, etc.
        tomtomArtifactory("maven-remotes")

        // PU IVI repo for internal dependencies.
        tomtomArtifactory("ivi-maven")
        tomtomArtifactory("ivi-test")

        // Repo for shared Android Tools like, UI Controls, resource resolutions,
        // viewcomparison test setup, animations...
        tomtomArtifactory("as-navapp-maven-release")
        tomtomArtifactory("nav-maven-release")
        tomtomArtifactory("nav-maven-dev")

        // PU NAV repo for NavTest and Gradle plugins from NAV.
        tomtomArtifactory("navapp-releases")

        // Analytics repo (used by NavApp components)
        tomtomArtifactory("dataunit-maven-release")

        // PU NAV repo for NavKit2
        tomtomArtifactory("navkit2-maven-release-local")
    }
}
