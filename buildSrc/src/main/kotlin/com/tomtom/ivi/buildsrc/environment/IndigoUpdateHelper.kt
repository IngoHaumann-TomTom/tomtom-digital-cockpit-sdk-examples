/*
 * Copyright © 2020 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, then you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.buildsrc.environment

import com.tomtom.ivi.buildsrc.environment.Libraries.TomTom.Indigo.PLATFORM_GROUP
import org.gradle.api.Project
import java.io.File

/**
 * This class retrieves the last available IndiGO product version available in Artifactory and
 * provides a method to replace the current INDIGO_PLATFORM version value in the 'Versions.kt' file.
 */
class IndigoUpdateHelper(private val project: Project) {

    private val latestIndigoVersion = project.property("latestIndigoVersion") as String

    /*
     * Generates a new Versions.kt file with the up-to-date IndiGO platform version.
     */
    fun generateNewVersionFile(oldFile: File, newFile: File) {
        val versionTag = "INDIGO_PLATFORM"
        val versionValue = Regex("""(\d+.\d+.\d+)""")
        oldFile.useLines { lines ->
            newFile.bufferedWriter().use { outFile ->
                lines.forEach { oldLine ->
                    val newLine = when {
                        oldLine.contains(versionTag) -> oldLine.replace(
                            versionValue,
                            latestIndigoVersion
                        )
                        else -> oldLine
                    }
                    if (oldLine.contains(versionTag)) {
                        val currentIndigoVersion: String? = versionValue.find(oldLine)?.value
                        println("Replacing $currentIndigoVersion IndiGO version with $latestIndigoVersion")
                    }
                    @Suppress("DEPRECATION")
                    outFile.appendln(newLine)
                }
            }
        }
    }
}
