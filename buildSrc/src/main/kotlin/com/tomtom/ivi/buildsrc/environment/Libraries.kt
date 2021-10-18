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

package com.tomtom.ivi.buildsrc.environment

/**
 * External libraries which modules depend on.
 */
object Libraries {
    object Android {
        const val ACTIVITY = "androidx.activity:activity-ktx:${Versions.ANDROIDX_ACTIVITY}"
        const val ANNOTATION = "androidx.annotation:annotation:${Versions.ANDROIDX_ANNOTATION}"
        const val KTX = "androidx.core:core-ktx:${Versions.ANDROIDX_KTX}"
        const val LIFECYCLE_SERVICE = "androidx.lifecycle:lifecycle-service:${Versions.ANDROIDX_LIFECYCLE}"
    }

    object Kotlin {
        const val SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLINX_SERIALIZATION}"
    }

    object TomTom {
        object Indigo {
            const val PLATFORM_GROUP = "com.tomtom.ivi.platform"
            const val API_COMMON_DEBUGTAB = "$PLATFORM_GROUP:api_common_debugtab:${Versions.INDIGO_PLATFORM}"
            const val API_COMMON_LIFECYCLE = "$PLATFORM_GROUP:api_common_lifecycle:${Versions.INDIGO_PLATFORM}"
            const val API_COMMON_UID = "$PLATFORM_GROUP:api_common_uid:${Versions.INDIGO_PLATFORM}"
            const val API_DEFAULTS_ACTIVITY = "$PLATFORM_GROUP:api_defaults_activity:${Versions.INDIGO_PLATFORM}"
            const val API_DEFAULTS_APPLICATION = "$PLATFORM_GROUP:api_defaults_application:${Versions.INDIGO_PLATFORM}"
            const val API_FRAMEWORK_FRONTEND = "$PLATFORM_GROUP:api_framework_frontend:${Versions.INDIGO_PLATFORM}"
            const val API_SERVICES_SETTINGSMANAGEMENT = "$PLATFORM_GROUP:api_services_settingsmanagement:${Versions.INDIGO_PLATFORM}"
            const val CORE_COMMON_THEME = "$PLATFORM_GROUP:core_common_theme:${Versions.INDIGO_PLATFORM}"
            const val CORE_COMMON_UICONTROLS = "$PLATFORM_GROUP:core_common_uicontrols:${Versions.INDIGO_PLATFORM}"
            const val CORE_COMMON_UIEMBEDDING = "$PLATFORM_GROUP:core_common_uiembedding:${Versions.INDIGO_PLATFORM}"
            const val CORE_CONFIG_NAVKIT2 = "$PLATFORM_GROUP:core_config_navkit2:${Versions.INDIGO_PLATFORM}"
            const val CORE_DEBUG_PERMISSIONS = "$PLATFORM_GROUP:core_debug_permissions:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_FRONTEND = "$PLATFORM_GROUP:tools_testing_frontend:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_FUNCTIONAL = "$PLATFORM_GROUP:tools_testing_functional:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_MAINMENUFRONTEND = "$PLATFORM_GROUP:tools_testing_mainmenufrontend:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_NAVIGATIONFUNCTIONAL = "$PLATFORM_GROUP:tools_testing_navigationfunctional:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_SYSTEMUI = "$PLATFORM_GROUP:tools_testing_systemui:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_UNIT = "$PLATFORM_GROUP:tools_testing_unit:${Versions.INDIGO_PLATFORM}"

            object ServiceApis {
                const val CORE_SERVICEAPIS_CONTACTS = "$PLATFORM_GROUP:core_serviceapis_contacts:${Versions.INDIGO_PLATFORM}"
                const val CORE_SERVICEAPIS_RECENTCALLS = "$PLATFORM_GROUP:core_serviceapis_recentcalls:${Versions.INDIGO_PLATFORM}"
                const val CORE_SERVICEAPIS_TELECOM = "$PLATFORM_GROUP:core_serviceapis_telecom:${Versions.INDIGO_PLATFORM}"
            }
        }

        object Automotive {
            const val ANDROID_CAR = "com.tomtom.ivi.ivi-automotive-sdk:android_car_lib:${Versions.ANDROID_CAR}"
        }

        object Tools {
            const val API_LIVEDATA = "com.tomtom.tools.android:api_livedata:${Versions.TOMTOM_ANDROID_TOOLS}"
        }

        object CommunicationsSdk {
            const val COMMON = "com.tomtom.ivi.sdk.communications:sdk_communications_common:${Versions.IVI_COMMUNICATIONS_SDK}"
            const val CLIENT = "com.tomtom.ivi.sdk.communications:sdk_communications_client:${Versions.IVI_COMMUNICATIONS_SDK}"
            const val EXAMPLESERVICE = "com.tomtom.ivi.sdk.communications:sdk_communications_exampleservice:${Versions.IVI_COMMUNICATIONS_SDK}"
        }
    }
}
