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

package com.tomtom.ivi.example.frontend.account

import androidx.lifecycle.map
import com.tomtom.ivi.api.common.notification.NotificationHeaderViewModel
import com.tomtom.ivi.api.common.notification.defaultnotification.DefaultNotificationPanel
import com.tomtom.ivi.api.common.notification.defaultnotification.DefaultNotificationViewModel
import com.tomtom.ivi.api.framework.frontend.Frontend
import com.tomtom.ivi.api.framework.frontend.FrontendContext
import com.tomtom.ivi.api.framework.frontend.FrontendFactory
import com.tomtom.ivi.api.framework.frontend.panels.NotificationPanel
import com.tomtom.ivi.api.framework.frontend.panels.filterPanels
import com.tomtom.ivi.core.common.util.ifTrue
import com.tomtom.ivi.example.frontend.account.info.AccountInfoPanel
import com.tomtom.ivi.example.frontend.account.login.AccountLoginPanel
import com.tomtom.ivi.example.frontend.account.notification.AccountNotificationPanel
import com.tomtom.ivi.example.frontend.account.notification.AccountNotificationViewModel
import com.tomtom.ivi.example.frontends.account.R
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.tools.android.api.resourceresolution.drawable.ResourceDrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.ResourceStringResolver
import com.tomtom.tools.android.api.resourceresolution.string.StaticStringResolver
import com.tomtom.tools.android.core.livedata.combine
import com.tomtom.tools.android.core.livedata.valueUpToDate

class AccountFrontend(frontendContext: FrontendContext) : Frontend(frontendContext) {

    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    private val isUserLoggedIn =
        combine(
            accountServiceApi.serviceAvailable,
            accountServiceApi.activeAccount
        ) { serviceAvailable, activeAccount ->
            serviceAvailable == true && activeAccount != null
        }

    private val account = accountServiceApi.activeAccount

    private val hasLoginPanel: Boolean
        get() = panels.valueUpToDate?.filterPanels<AccountLoginPanel>()?.size == 1

    private val hasInfoPanel: Boolean
        get() = panels.valueUpToDate?.filterPanels<AccountInfoPanel>()?.size == 1

    private fun showLoginNotification() {
        val username = account.valueUpToDate?.let { StaticStringResolver(it.username) }
            ?: ResourceStringResolver(R.string.frontend_account_notification_invalid_username)

        addPanel(
            AccountNotificationPanel(
                frontendContext = frontendContext,
                userName = username
            )
        )
    }

    override fun onCreate() {
        super.onCreate()

        isUserLoggedIn.observe(this) { loggedIn ->
            // Switch panels if an user login status changed, if there is an open panel.
            when {
                loggedIn -> {
                    closeAllTaskPanels()
                    showLoginNotification()
                }
                !loggedIn && hasInfoPanel -> goToLoginPanel()
            }
        }
    }

    override fun openTaskPanels() {
        if (isUserLoggedIn.valueUpToDate == true) {
            goToInfoPanel()
        } else {
            goToLoginPanel()
        }
    }

    private fun goToInfoPanel() {
        closeAllTaskPanels()
        addPanel(AccountInfoPanel(frontendContext))
    }

    private fun goToLoginPanel() {
        closeAllTaskPanels()
        addPanel(AccountLoginPanel(frontendContext))
    }

    class Factory : FrontendFactory {
        override fun createFrontend(frontendContext: FrontendContext) =
            AccountFrontend(frontendContext)
    }
}
