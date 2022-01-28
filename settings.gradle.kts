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

rootProject.name = "IVI_Example"

apply(from = "build-logic/repositories.gradle.kts")
apply(from = "build-logic/libraries.versioncatalog.gradle.kts")

/**
 * Note:
 * This plugin configuration file is intended for TomTom internal use and not relevant
 * to an external developer so it can be removed
 */
val enterprisePluginConfig: File = file("${rootProject.projectDir}/build-logic/gradle.enterprise.gradle.kts")
if (enterprisePluginConfig.exists()) {
    apply(from = enterprisePluginConfig.path)
}

val modulesDir: File = file("${rootProject.projectDir}/modules/")
fileTree(modulesDir)
    .matching { include("*/*/build.gradle.kts") }
    .forEach { file ->
        val projectName = ":" + file.toProjectName(modulesDir)
        include(projectName)
        project(projectName).projectDir = file.parentFile
    }

fun File.toProjectName(modulesDir: File): String = modulesDir.toPath()
    .relativize(parentFile.toPath())
    .joinToString("_")
