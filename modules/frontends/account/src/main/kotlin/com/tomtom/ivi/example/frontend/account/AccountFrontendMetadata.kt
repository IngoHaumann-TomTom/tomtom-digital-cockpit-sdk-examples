/*
 * Copyright (c) 2020 - 2020 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.ivi.example.frontend.account

import com.tomtom.ivi.api.common.menu.MenuItem
import com.tomtom.ivi.api.framework.frontend.FrontendMetadata
import com.tomtom.ivi.api.framework.frontend.FrontendStartupPolicy
import com.tomtom.ivi.example.frontends.account.R

val accountFrontendMetadata = FrontendMetadata(
    AccountFrontend.Factory(),
    FrontendStartupPolicy.LAUNCH_FRONTEND_ON_DEMAND,
    MenuItem(
        AccountFrontend::class.qualifiedName!!,
        R.drawable.frontend_account_menu_item_icon,
        R.string.frontend_account_menu_item_name
    )
)
