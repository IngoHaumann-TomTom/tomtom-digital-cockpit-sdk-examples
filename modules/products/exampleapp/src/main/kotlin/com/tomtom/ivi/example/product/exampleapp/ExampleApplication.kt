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

package com.tomtom.ivi.example.product.exampleapp

import com.tomtom.ivi.platform.framework.api.product.defaultapplication.DefaultApplication
import com.tomtom.ivi.platform.navkit2.api.common.configuration.Navkit2ApiKeyStaticConfigurationProvider

@Suppress("unused")
internal class ExampleApplication : DefaultApplication() {

    /**
     * The following [createStaticConfigurationProviders] implementation serves as an example for
     * configuring the NavKit2 API key with a [Navkit2ApiKeyStaticConfigurationProvider] instance,
     * if [CONFIGURE_NAVKIT2_API_KEY_STATIC_CONFIGURATION_PROVIDER] is set to `true`.
     *
     * By default [CONFIGURE_NAVKIT2_API_KEY_STATIC_CONFIGURATION_PROVIDER] is set to `false`,
     * as it is also possible to configure the NavKit2 API key without any code changes. You can
     * do this by setting the `navkit2ApiKey` property to your NavKit2 API key in the top-level
     * `local.properties` file or as a Gradle property. This approach is preferred if you just
     * want to get it working.
     *
     * It is also possible to configure the NavKit2 API key by overriding the Android resources
     * value. See for this the `navkit2apikeys.xml` file in `src/res/values` directory of this
     * project.
     */
    override fun createStaticConfigurationProviders() =
        if (CONFIGURE_NAVKIT2_API_KEY_STATIC_CONFIGURATION_PROVIDER) {
            @Suppress("UNREACHABLE_CODE")
            /**
             * The [Navkit2ApiKeyStaticConfigurationProvider] intends to override values from
             * provider(s) in the parent class and therefore must be first in the list of
             * providers.
             */
            listOf(
                Navkit2ApiKeyStaticConfigurationProvider(navkit2ApiKey = NAVKIT2_API_KEY)
            ) + super.createStaticConfigurationProviders()
        } else {
            super.createStaticConfigurationProviders()
        }

    companion object {
        /**
         * Set this to `true` to configure the NavKit2 API key via a
         * [Navkit2ApiKeyStaticConfigurationProvider] instance. In this case, you also need to:
         *
         * - Implement the [NAVKIT2_API_KEY] property below.
         * - Set the `disableNavkit2ApiKeyBuildTimeCheck` property to `true` as a Gradle
         *   property or do not apply the `com.tomtom.ivi.platform.defaults.navkit2` Gradle plugin.
         */
        private const val CONFIGURE_NAVKIT2_API_KEY_STATIC_CONFIGURATION_PROVIDER = false

        /**
         * The NavKit2 API key that is configured when
         * [CONFIGURE_NAVKIT2_API_KEY_STATIC_CONFIGURATION_PROVIDER] is set to `true.
         *
         * Must be implemented when [CONFIGURE_NAVKIT2_API_KEY_STATIC_CONFIGURATION_PROVIDER]
         * is set to `true`.
         */
        private val NAVKIT2_API_KEY by lazy<String> {
            @Suppress("StopShip")
            TODO("Implement obtaining the NavKit2 API key.")
        }
    }
}
