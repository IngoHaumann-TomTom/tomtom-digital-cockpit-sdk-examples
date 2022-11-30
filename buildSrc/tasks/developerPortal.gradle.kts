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

val portalDirectory = "${project.projectDir}/docs/portal"
val targetDir = "${portalDirectory}/build/export"
val iviPlatformVersion = libraries.versions.iviPlatform.get()
val iviCommsVersion = iviDependencies.versions.iviCommunicationsSdk.get()
val androidToolsVersion = iviDependencies.versions.tomtomAndroidTools.get()

val artifactoryBaseUrl: String? by project
val artifactoryUser: String? by project
val artifactoryToken: String? by project

/**
 * TomTom internal tooling, see docs/portal/README.md if you are a TomTom developer.
 *
 */
tasks.register<Exec>("portal_check") {
    description = "Validates Developer Portal content."
    group = "Documentation"

    val artifactoryUrl = artifactoryBaseUrl ?: "https://artifactory.tomtomgroup.com/artifactory"
    workingDir(portalDirectory)
    commandLine("python3")
    args("-B", "scripts/portal_generator.py",
        iviPlatformVersion,
        iviCommsVersion,
        androidToolsVersion,
        targetDir,
        artifactoryUrl,
        artifactoryUser,
        artifactoryToken
        )
}

/**
 * TomTom internal tooling, see docs/portal/README.md if you are a TomTom developer.
 */
tasks.register<Exec>("portal_export") {
    description = "Generates Developer Portal content for export."
    group = "Documentation"

    workingDir(portalDirectory)
    commandLine("python3")
    args("-B", "scripts/portal_generator.py",
        iviPlatformVersion,
        iviCommsVersion,
        androidToolsVersion,
        targetDir,
        artifactoryBaseUrl,
        artifactoryUser,
        artifactoryToken,
        "export"
        )
}
