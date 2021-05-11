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

package com.tomtom.ivi.example.frontend.account.notification

import com.tomtom.ivi.api.common.notification.NotificationFragment
import com.tomtom.ivi.api.framework.frontend.FrontendContext
import com.tomtom.ivi.api.framework.frontend.IviFragment
import com.tomtom.ivi.api.framework.frontend.panels.NotificationPanel
import com.tomtom.tools.android.api.resourceresolution.string.StringResolver

class AccountNotificationPanel(
    frontendContext: FrontendContext,
    val userName: StringResolver,
) : NotificationPanel(frontendContext, Priority.HIGH) {

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(
            NotificationFragment(AccountNotificationViewModel::class),
            this
        )
}