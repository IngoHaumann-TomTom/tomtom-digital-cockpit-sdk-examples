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

import com.tomtom.ivi.buildsrc.environment.IndigoUpdateHelper

tasks.register("generateIndigoLibrariesVersionFile") {
    group = "Help"
    description = "Checks for IndiGO library updates and generates a new Versions.kt file if any update is found."
    doLast {
        val indigoUpdateHelper = IndigoUpdateHelper(rootProject)
        val newVersionFile = File(projectDir, "Versions.kt")
        fileTree(projectDir).find { it.name == "Versions.kt" }?.let {
            indigoUpdateHelper.generateNewVersionFile(it, newVersionFile)
        }
    }
}
