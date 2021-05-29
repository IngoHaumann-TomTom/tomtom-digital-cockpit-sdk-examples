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

package com.tomtom.ivi.buildsrc.common.signingconfig

import com.tomtom.ivi.buildsrc.extensions.android
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * The project can use this plugin to sign it with the keystore file specified by the gradle
 * property: `applicationKeystore`. The keystore password file is specified by the gradle property:
 * `applicationKeystorePassword`
 *
 * The key alias of each signing config is defined as `tomtom-<signing-config-name>`. For instance
 * `tomtom-release` or `tomtom-debug`.
 */
class SigningConfigPlugin : Plugin<Project> {

    override fun apply(targetProject: Project) {
        targetProject.android.signingConfigs {
            val rootProject = targetProject.rootProject
            val keystoreFile =
                rootProject.file(rootProject.property("applicationKeystore").toString())
            val passwordFile =
                rootProject.file(rootProject.property("applicationKeystorePassword").toString())
            val keystorePassword = passwordFile.readText()
            require(
                keystoreFile.canRead() && passwordFile.canRead()
                    && keystorePassword.isNotEmpty()
            ) {
                "Unable to read keystore files, " +
                    "path=${keystoreFile.path}, " +
                    "project=${targetProject.name}'."
            }
            all {
                storeFile = keystoreFile
                keyAlias = "tomtom-${name}"
                storePassword = keystorePassword
                keyPassword = keystorePassword
            }
        }
    }
}
