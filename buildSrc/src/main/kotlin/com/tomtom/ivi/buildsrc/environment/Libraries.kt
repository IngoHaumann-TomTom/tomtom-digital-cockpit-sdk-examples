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
            const val CORE_COMMON_THEME = "$PLATFORM_GROUP:core_common_theme:${Versions.INDIGO_PLATFORM}"
            const val CORE_CONFIG_NAVKIT2 = "$PLATFORM_GROUP:core_config_navkit2:${Versions.INDIGO_PLATFORM}"
            const val DEBUG_API_FRONTENDEXTENSION_DEBUGTAB = "$PLATFORM_GROUP:platform_debug_api_frontendextension_debugtab:${Versions.INDIGO_PLATFORM}"
            const val FRAMEWORK_API_COMMON_LIFECYCLE = "$PLATFORM_GROUP:platform_framework_api_common_lifecycle:${Versions.INDIGO_PLATFORM}"
            const val FRAMEWORK_API_COMMON_UID = "$PLATFORM_GROUP:platform_framework_api_common_uid:${Versions.INDIGO_PLATFORM}"
            const val FRAMEWORK_API_COMMON_UIEMBEDDING = "$PLATFORM_GROUP:platform_framework_api_common_uiembedding:${Versions.INDIGO_PLATFORM}"
            const val FRAMEWORK_API_PRODUCT_DEBUGPERMISSIONS = "$PLATFORM_GROUP:platform_framework_api_product_debugpermissions:${Versions.INDIGO_PLATFORM}"
            const val FRAMEWORK_API_PRODUCT_DEFAULTACTIVITY = "$PLATFORM_GROUP:platform_framework_api_product_defaultactivity:${Versions.INDIGO_PLATFORM}"
            const val FRAMEWORK_API_PRODUCT_DEFAULTAPPLICATION = "$PLATFORM_GROUP:platform_framework_api_product_defaultapplication:${Versions.INDIGO_PLATFORM}"
            const val FRONTEND_API_COMMON_FRONTEND = "$PLATFORM_GROUP:platform_frontend_api_common_frontend:${Versions.INDIGO_PLATFORM}"
            const val SETTINGS_API_SERVICES_SETTINGSMANAGEMENT = "$PLATFORM_GROUP:platform_settings_api_service_settingsmanagement:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_FRONTEND = "$PLATFORM_GROUP:tools_testing_frontend:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_FUNCTIONAL = "$PLATFORM_GROUP:tools_testing_functional:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_MAINMENUFRONTEND = "$PLATFORM_GROUP:tools_testing_mainmenufrontend:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_NAVIGATIONFUNCTIONAL = "$PLATFORM_GROUP:tools_testing_navigationfunctional:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_SYSTEMUI = "$PLATFORM_GROUP:tools_testing_systemui:${Versions.INDIGO_PLATFORM}"
            const val TOOLS_TESTING_UNIT = "$PLATFORM_GROUP:tools_testing_unit:${Versions.INDIGO_PLATFORM}"

            object ServiceApis {
                const val CORE_SERVICEAPIS_CONTACTS = "$PLATFORM_GROUP:core_serviceapis_contacts:${Versions.INDIGO_PLATFORM}"
                const val CORE_SERVICEAPIS_TELECOM = "$PLATFORM_GROUP:core_serviceapis_telecom:${Versions.INDIGO_PLATFORM}"
                const val PLATFORM_RECENTCALLS_API_SERVICE_RECENTCALLS = "$PLATFORM_GROUP:platform_recentcalls_api_service_recentcalls:${Versions.INDIGO_PLATFORM}"
            }
        }

        object Automotive {
            const val ANDROID_CAR = "com.tomtom.ivi.ivi-automotive-sdk:android_car_lib:${Versions.ANDROID_CAR}"
        }

        object Tools {
            const val API_LIVEDATA = "com.tomtom.tools.android:api_livedata:${Versions.TOMTOM_ANDROID_TOOLS}"
            const val API_UICONTROLS = "com.tomtom.tools.android:api_uicontrols:${Versions.TOMTOM_ANDROID_TOOLS}"
        }

        object CommunicationsSdk {
            const val COMMON = "com.tomtom.ivi.sdk.communications:sdk_communications_common:${Versions.IVI_COMMUNICATIONS_SDK}"
            const val CLIENT = "com.tomtom.ivi.sdk.communications:sdk_communications_client:${Versions.IVI_COMMUNICATIONS_SDK}"
            const val EXAMPLESERVICE = "com.tomtom.ivi.sdk.communications:sdk_communications_exampleservice:${Versions.IVI_COMMUNICATIONS_SDK}"
        }
    }
}
