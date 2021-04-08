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

import java.io.ByteArrayOutputStream
import org.gradle.api.Project

sealed class VersioningAccessBase {
    abstract val project: Project

    abstract val branch: String
    abstract val tag: String
    abstract val description: String
    abstract val commitCount: Int

    protected fun git(gitCommand: String): String =
        command("git", gitCommand.split(" "))

    protected fun command(program: String, arguments: List<String>): String {
        val output = ByteArrayOutputStream()
        project.exec {
            executable = program
            args = arguments
            standardOutput = output
            errorOutput = ByteArrayOutputStream()
            workingDir = project.rootDir
            isIgnoreExitValue = true
        }
        return output.toString().trim()
    }
}

/**
 * On CI, the merging environment places HEAD without reference, making it unreliable
 * to use references to `master` (which would be behind by one commit in almost all cases).
 * Using GitVersion makes this better, and additionally, it also ensures the
 * output format conforms to SemVer standard.
 *
 * This assumes the tool `gitversion` - https://github.com/GitTools/GitVersion - is available.
 * Note that GitVersion does not support multiple remotes, thus making it unsuitable for local
 * use in the forking development model.
 */
class VersioningAccessCi(override val project: Project) : VersioningAccessBase() {
    override val tag: String by lazy { gitVersion("SemVer") }
    override val branch: String by lazy { gitVersion("BranchName") }
    override val description: String by lazy { gitVersion("FullSemVer") }
    override val commitCount: Int by lazy {
        // Not using gitversion here, its commit count resets to 0 on tagged releases.
        git("rev-list --count HEAD").toInt()
    }

    private fun gitVersion(variable: String): String =
        command(
            "gitversion", listOf(
                "/nocache",
                "/verbosity",
                "error",
                "/showvariable",
                variable
            )
        )
}

/**
 * On local builds, we can rely on HEAD being placed on a branch created out of master,
 * allowing direct usage of Git.
 */
class VersioningAccessLocal(override val project: Project) : VersioningAccessBase() {
    override val tag: String by lazy {
        git("describe --tags --exact-match HEAD")
    }

    override val branch: String by lazy {
        git("rev-parse --abbrev-ref HEAD")
    }

    override val description: String by lazy {
        git("describe --tags --abbrev=7 --dirty --first-parent")
    }

    override val commitCount: Int by lazy {
        git("rev-list --count HEAD").toInt()
    }
}
