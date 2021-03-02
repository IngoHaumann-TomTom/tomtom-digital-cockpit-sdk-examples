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

import com.tomtom.ivi.buildsrc.environment.Libraries.TomTom.Indigo.PLATFORM_GROUP
import kotlin.text.Regex
import java.io.File
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * This class retrieves the last available IndiGO product version available in Artifactory and
 * provides a method to replace the current INDIGO_PLATFORM version value in the 'Versions.kt' file.
 */
class IndigoUpdateHelper(val project: Project) {

    private val latestIndigoVersion: String =
        project.configurations.create("latestIndigoReleaseConfiguration")
            .apply {
                val latestIndigoDependency = project.allprojects
                    .flatMap { it.configurations }
                    .flatMap { it.dependencies }
                    .filter { it.group == PLATFORM_GROUP && it.name.startsWith("api_") }
                    .map { project.dependencies.create("${it.group}:${it.name}:latest.release") }
                    .first()
                dependencies.add(latestIndigoDependency)
            }
            .resolvedConfiguration.firstLevelModuleDependencies
            .map { it.moduleVersion }
            .first()

    /*
     * Generates a new Versions.kt file with the up-to-date IndiGO platform version.
     */
    fun generateNewVersionFile(oldFile: File, newFile: File) {
        val versionTag = "INDIGO_PLATFORM"
        val versionValue = Regex("""(\d+.\d+.\d+)""")

        println("Replacing current IndiGO version with " + latestIndigoVersion)

        oldFile.useLines { lines ->
            newFile.bufferedWriter().use { outFile ->
                lines.forEach { oldLine ->
                    val newLine = when {
                        oldLine.contains(versionTag) -> oldLine.replace(versionValue, latestIndigoVersion)
                        else -> oldLine
                    }
                    outFile.appendln(newLine)
                }
            }
        }
    }
}
