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

package com.tomtom.ivi.example.product.exampleapp

import com.tomtom.ivi.api.defaults.application.DefaultApplication
import com.tomtom.ivi.core.config.navkit2.Navkit2ApiKeyStaticConfigurationProvider
import com.tomtom.ivi.navkit2apikey.NAVKIT2_API_KEY

class ExampleApplication : DefaultApplication() {
    
    /**
     * Creates a collection of [ComposableStaticConfigurationProvider]s used to resolve a static
     * configuration lookup request.
     *
     * The [Navkit2ApiKeyStaticConfigurationProvider] intends to override values from provider(s)
     * in the parent class and therefore must be first in the list of providers.
     */
    override fun createStaticConfigurationProviders() =
        listOf(
            // TODO(IVI-3179): Replace IVI_NAVKIT_API_KEY with a dummy string.
            // TODO(IVI-3179): Document how OEM must use their own navkit api-key.
            Navkit2ApiKeyStaticConfigurationProvider(navkit2ApiKey = NAVKIT2_API_KEY)
        ) + super.createStaticConfigurationProviders()
}
