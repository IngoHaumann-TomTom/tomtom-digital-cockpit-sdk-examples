/*
 * Copyright (c) 2020 - 2020 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

val modulesDir: File = file("${rootProject.projectDir}/modules/")
fileTree(modulesDir)
    .matching { include("*/*/build.gradle.kts") }
    .forEach { file ->
        val parentDir = file.parentFile
        val projectName = ":" + modulesDir.toPath().relativize(parentDir.toPath()).joinToString("_")

        include(projectName)
        project(projectName).projectDir = parentDir
    }

rootProject.name = "IVI Example"