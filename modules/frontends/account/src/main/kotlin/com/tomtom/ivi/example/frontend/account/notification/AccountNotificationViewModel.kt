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

import com.tomtom.ivi.api.common.notification.NotificationHeaderStyling
import com.tomtom.ivi.api.common.notification.NotificationHeaderViewModel
import com.tomtom.ivi.api.common.notification.NotificationViewModel
import com.tomtom.ivi.example.frontends.account.R
import com.tomtom.tools.android.api.resourceresolution.string.ResourceStringResolver

class AccountNotificationViewModel(panel: AccountNotificationPanel) :
    NotificationViewModel<AccountNotificationPanel>(panel) {

    override val headerViewModel = NotificationHeaderViewModel(
        headerTitle = ResourceStringResolver(
            R.string.frontend_account_notification_title_text, panel.userName
        ),
        headerDescription = ResourceStringResolver(
            R.string.frontend_account_notification_description_text, panel.userName
        ),
        headerStyling = NotificationHeaderStyling(headerDescriptionMultiLine = true)
    )
}