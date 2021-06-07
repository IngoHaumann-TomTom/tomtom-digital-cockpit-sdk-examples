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

package com.tomtom.ivi.buildsrc.config.services

import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.gradle.api.common.dependencies.IviPlatformModuleReference
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServiceInterfaceConfig

/**
 * Defines a configuration for the recent calls service.
 *
 * The configuration specifies the service host implementation for the recent calls service
 * interface.
 */
val customRecentCallsServiceHost = IviServiceHostConfig(
    serviceHostBuilderName = "CustomRecentCallsServiceHostBuilder",
    implementationModule = ExampleModuleReference("services_customrecentcalls"),
    interfaces = listOf(
        IviServiceInterfaceConfig(
            serviceName = "RecentCallsService",
            serviceApiModule = IviPlatformModuleReference("core_serviceapis_recentcalls")
        )
    )
)

