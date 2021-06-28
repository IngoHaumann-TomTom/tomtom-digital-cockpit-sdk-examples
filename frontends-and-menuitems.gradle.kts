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

import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.gradle.api.common.iviapplication.config.FrontendCreationPolicy
import com.tomtom.ivi.gradle.api.common.iviapplication.config.FrontendConfig

/**
 * This file defines the frontend implementations and the menu item implementations used
 * in the examples. The frontend implementations and the menu item implementations are defined in
 * this file so they can be used in all Gradle subprojects, including tests.
 *
 * The properties defined in this file can be accessed in the `build.gradle.kts` files by applying
 * this file in the `build.gradle.kts` file and by using Gradle's extra extension.
 *
 * ```kotlin
 * import com.tomtom.ivi.gradle.api.common.iviapplication.config.FrontendConfig
 * import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviInstanceIdentifier
 * import com.tomtom.ivi.gradle.api.common.iviapplication.config.MenuItemConfig
 * import com.tomtom.ivi.gradle.api.plugin.platform.ivi
 *
 * // Define the frontends and menu items as defined in top level
 * // `frontends-and-menuitems.gradle.kts` file.
 * apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))
 *
 * // Use Gradle's extra extensions to obtain the `accountFrontend` and `accountMenuItem` configs as
 * // defined in the top level `frontends-and-menuitems.gradle.kts` file.
 * val accountFrontend: FrontendConfig by project.extra
 * val accountMenuItem: MenuItemConfig by project.extra
 *
 * plugins {
 *     // Apply the plugin to use to default frontends and menu items.
 *     id("com.tomtom.ivi.defaults.core")
 * }
 *
 * ivi {
 *     application {
 *         enabled = true
 *         iviInstances {
 *             create(IviInstanceIdentifier.default) {
 *                 // Use the defaults frontends and menu items as defined by the
 *                 // `com.tomtom.ivi.defaults.core` plugin.
 *                 useDefaults()
 *                 frontends {
 *                     // Register the `accountFrontend`.
 *                     add(accountFrontend)
 *                 }
 *                 menuItems {
 *                     // Register the `accountMenuItem` and associate it to the `accountFrontend`.
 *                     addLast(accountMenuItem to accountFrontend)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ...
 * ```
 *
 * The above example obtains the `accountFrontend` and the `accountMenuItem` properties as defined
 * in this file and adds them to the IVI application.
 *
 * To allow the Gradle's extra extensions to work, the properties in this file need to use the
 * `by extra` delegation to assign the value.
 */

val accountFrontend by extra {
    FrontendConfig(
        frontendBuilderName = "AccountFrontendBuilder",
        implementationModule = ExampleModuleReference("frontends_account"),
        creationPolicy = FrontendCreationPolicy.CREATE_ON_DEMAND
    )
}

val accountMenuItem by extra {
    accountFrontend.toMenuItem("accountMenuItem")
}
