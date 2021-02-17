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

/**
 * External libraries which modules depend on.
 */
object Libraries {
    object Android {
        const val ANNOTATION = "androidx.annotation:annotation:${Versions.ANDROIDX_ANNOTATION}"
        const val KTX = "androidx.core:core-ktx:${Versions.ANDROIDX_KTX}"
    }

    object TomTom {
        object Indigo {
            const val PLATFORM_GROUP = "com.tomtom.ivi.platform"
            const val API_COMMON_MENU = "$PLATFORM_GROUP:api_common_menu:${Versions.INDIGO_PLATFORM}"
            const val API_DEFAULTS_ACTIVITY = "$PLATFORM_GROUP:api_defaults_activity:${Versions.INDIGO_PLATFORM}"
            const val API_DEFAULTS_APPLICATION = "$PLATFORM_GROUP:api_defaults_application:${Versions.INDIGO_PLATFORM}"
            const val API_FRAMEWORK_FRONTEND = "$PLATFORM_GROUP:api_framework_frontend:${Versions.INDIGO_PLATFORM}"
            const val CORE_COMMON_THEME = "$PLATFORM_GROUP:core_common_theme:${Versions.INDIGO_PLATFORM}"
            const val CORE_COMMON_UICONTROLS = "$PLATFORM_GROUP:core_common_uicontrols:${Versions.INDIGO_PLATFORM}"
            const val CORE_DEBUG_PERMISSIONS = "$PLATFORM_GROUP:core_debug_permissions:${Versions.INDIGO_PLATFORM}"
        }

        object Automotive {
            const val ANDROID_CAR = "com.tomtom.ivi.ivi-automotive-sdk:android_car_lib:${Versions.ANDROID_CAR}"
        }
    }

}
