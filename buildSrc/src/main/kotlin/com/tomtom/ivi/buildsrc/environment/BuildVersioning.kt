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

package com.tomtom.ivi.buildsrc.environment

import org.gradle.api.Project

/**
 * TODO(IVI-2890): Use the Gradle plugin.
 */
class BuildVersioning(private val project: Project) {

    /**
     * Note: This works only on Azure DevOps.
     */
    val isCiBuild: Boolean = System.getenv("TF_BUILD")?.isNotEmpty() ?: false

    private val versions: VersioningAccessBase =
        if (isCiBuild) {
            VersioningAccessCi(project)
        } else {
            VersioningAccessLocal(project)
        }

    val canPublish: Boolean by lazy {
        branch.isNotEmpty() &&
            branch.matches(Regex("^(master|release/.+)$")) &&
        tag.isNotEmpty() &&
            tag.matches(Regex("^[0-9.]{5,}$"))
    }

    val tag: String by lazy { versions.tag }
    val branch: String by lazy { versions.branch }
    val description: String by lazy { versions.description }
    val versionCode: Int by lazy { versions.commitCount }
    val versionName: String by lazy {
        if (canPublish) {
            tag
        } else {
            "${description}-${branch.replace("/", "_")}"
        }
    }

    fun exportVersionInfoToFile() {
        val buildDir = project.buildDir.absolutePath
        if (!project.file(buildDir).isDirectory) {
            if (!project.mkdir(buildDir).exists()) {
                throw RuntimeException("Unable to create build directory for CI version files")
            }
        }
        val versionFile = project.file("$buildDir/version.txt")
        versionFile.writeText(description)
        val publishFile = project.file("$buildDir/canPublish.txt")
        publishFile.writeText(canPublish.toString())
    }

    fun print() {
        println("""
            Is CI build:      ${if (isCiBuild) "yes" else "no"}
            Can be published: ${if (canPublish) "yes" else "no"}
            Tag:              ${if (tag.isNotEmpty()) tag else "<none>"}
            Branch:           $branch
            Description:      $description
            Version code:     $versionCode
            Version name:     $versionName
            """.trimIndent()
        )
    }
}
