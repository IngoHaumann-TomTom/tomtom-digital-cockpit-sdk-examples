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

package com.tomtom.ivi.buildsrc.dependencies

import com.tomtom.ivi.gradle.api.common.dependencies.ModuleReference

/**
 * Example [ModuleReference] extension.
 *
 * Used in this project to reference modules in the build config. It converts the module names to
 * package names.
 *
 * For example, the module `services_account` is converted into the package
 * `com.tomtom.ivi.example.service.account`.
 */
class ExampleModuleReference(moduleName: String) : ModuleReference(
    groupName = GROUP_NAME,
    moduleName = moduleName,
    packageName = convertModuleNameToPackageName(moduleName)
) {
    companion object {
        private fun convertModuleNameToPackageName(moduleName: String): String {
            val hierarchy = moduleName.split("_", limit = 2)

            val category = hierarchy[0].let {
                when (it) {
                    "services" -> "service"
                    "serviceapis" -> "serviceapi"
                    else -> it
                }
            }
            val module = hierarchy[1]
            return "$GROUP_NAME.$ROOT_NAME.$category.$module"
        }

        private const val ROOT_NAME = "example"
        private const val GROUP_NAME = "com.tomtom.ivi"
    }
}
