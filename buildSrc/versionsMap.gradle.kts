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

/**
 * Workaround to load versions from this module's version object. This is needed because
 * this module's build script needs to know the versions of the build tools in order to build the
 * versions object. To resolve this cyclic dependency, we parse the code file manually. This
 * approach should be avoided anywhere else as Versions can just be accessed directly.
 */
private val versionsFile = "Versions.kt"
private val versionRegex = "\\s*const val (\\w+) *= *\"([^\"]+)\"".toRegex()
val versions: Map<String, String> by extra {
    fileTree(projectDir)
        .find { it.name == versionsFile }
        ?.readLines()
        ?.mapNotNull { versionRegex.find(it)?.groups }
        ?.associate { it[1]!!.value to it[2]!!.value }!!
}