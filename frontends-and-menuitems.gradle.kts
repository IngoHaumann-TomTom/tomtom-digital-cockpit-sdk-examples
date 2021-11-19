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

import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.dependencies.IviPlatformModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendCreationPolicy
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendExtensionConfig

/**
 * This file defines the frontend implementations and the menu item implementations used
 * in the examples. The frontend implementations and the menu item implementations are defined in
 * this file so they can be used in all Gradle subprojects, including tests.
 *
 * The properties defined in this file can be accessed in the `build.gradle.kts` files by applying
 * this file in the `build.gradle.kts` file and by using Gradle's extra extension.
 *
 * ```kotlin
 * import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
 * import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
 * import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
 * import com.tomtom.ivi.platform.gradle.api.framework.config.ivi
 *
 * // Define the frontends and menu items as defined in top-level
 * // `frontends-and-menuitems.gradle.kts` file.
 * apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))
 *
 * // Use Gradle's extra extensions to obtain the `accountFrontend` and `accountMenuItem` configs as
 * // defined in the top-level `frontends-and-menuitems.gradle.kts` file.
 * val accountFrontend: FrontendConfig by project.extra
 * val accountMenuItem: MenuItemConfig by project.extra
 *
 * plugins {
 *     // Apply the plugin to use to default frontends and menu items.
 *     id("com.tomtom.ivi.platform.defaults.core")
 * }
 *
 * ivi {
 *     application {
 *         enabled = true
 *         iviInstances {
 *             create(IviInstanceIdentifier.default) {
 *                 // Use the defaults frontends and menu items as defined by the
 *                 // `com.tomtom.ivi.platform.defaults.core` plugin.
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

val hierarchyFrontend by extra {
    FrontendConfig(
        frontendBuilderName = "HierarchyFrontendBuilder",
        implementationModule = ExampleModuleReference("frontends_hierarchy"),
        creationPolicy = FrontendCreationPolicy.CREATE_ON_DEMAND
    )
}

val hierarchyMenuItem by extra {
    hierarchyFrontend.toMenuItem("hierarchyMenuItem")
}

/**
 * This [project] [extra] refers to a frontend that comes from the IndiGO platform.
 * The debug frontend displays on request a non-user-facing development panel.
 */
val debugFrontend by extra {
    FrontendConfig(
        frontendBuilderName = "DebugFrontendBuilder",
        implementationModule = IviPlatformModuleReference("stock_frontends_debug")
    )
}

/**
 * This [project] [extra] refers to the `ActivityView` debug tab frontend extension.
 * This debug panel tab is also a showcase for modal panels and 3rd party activity embedding.
 */
val activityViewDebugTabFrontendExtension by extra {
    FrontendExtensionConfig(
        frontendExtensionName = "activityViewDebugTabFrontendExtension",
        implementationModule = ExampleModuleReference("debugtabs_activityview")
    )
}
