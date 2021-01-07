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

import com.tomtom.ivi.api.defaults.activity.DefaultActivity
import com.tomtom.ivi.api.defaults.frontends.defaultFrontendMetadata
import com.tomtom.ivi.api.framework.frontend.FrontendMetadata
import com.tomtom.ivi.api.frontend.userprofile.userProfileFrontendMetadata
import com.tomtom.ivi.example.frontend.account.accountFrontendMetadata

class ExampleActivity : DefaultActivity() {
    override val frontendMetadata: Collection<FrontendMetadata> =
        defaultFrontendMetadata + frontendsToAdd - frontendsToRemove

    companion object {
        private val frontendsToAdd = listOf(
            accountFrontendMetadata
        )
        private val frontendsToRemove = listOf(
            userProfileFrontendMetadata
        )
    }
}