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

package com.tomtom.ivi.example.functionaltest.integration

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tomtom.ivi.core.serviceapi.menu.MenuServiceMock
import com.tomtom.ivi.example.frontend.account.accountMenuItem
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.AccountServiceApi
import com.tomtom.ivi.example.serviceapi.account.SensitiveString
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.tools.servicemockextensions.menuservice.injectMenuItemClicked
import com.tomtom.ivi.tools.testing.frontend.FrontendTestCase
import com.tomtom.ivi.tools.testing.functional.util.waitForLiveData
import com.tomtom.tools.android.testing.functional.waitForView
import com.tomtom.tools.android.testing.functional.withIdReference
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class AccountFrontendTest : FrontendTestCase() {
    // An API wrapper cannot be created before test environment is fully configured, hence create
    // it later with @Before method.
    private lateinit var accountServiceApi: AccountServiceApi

    @Before
    fun createServiceApi() {
        accountServiceApi = createApiWrapper { lifecycleOwner, iviServiceProvider ->
            AccountService.createApi(lifecycleOwner, iviServiceProvider)
        }
        assertTrue(accountServiceApi.serviceAvailable.waitForLiveData { value -> value })
    }

    @After
    fun ensureNoUserLoggedIn() = runBlocking {
        accountServiceApi.coLogOut()
    }

    @Test
    fun frontendOpensLoginPanelIfNoUserHasLoggedIn() {
        // GIVEN No user has logged in.
        // WHEN The frontend is invoked from the menu.
        tapAccountMenuItem()
        // THEN The login view is open.
        waitForView(thatIsAccountLoginView).check(matches(isDisplayed()))
    }

    @Test
    fun frontendOpensInfoPanelIfUserHasLoggedIn() {
        // GIVEN The user has logged in.
        runBlocking {
            accountServiceApi.coLogIn("username", SensitiveString("password"))
        }

        // WHEN The frontend is invoked from the menu.
        tapAccountMenuItem()
        // THEN The info view is open, with the correct name in the title.
        waitForView(thatIsAccountInfoPanel)
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withText("Welcome, Username"))))

        // WHEN The frontend is closed.
        tapAccountMenuItem()
        // AND The frontend is open again.
        tapAccountMenuItem()

        // THEN The info view is open, with the correct name in the title.
        waitForView(thatIsAccountInfoPanel)
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withText("Welcome, Username"))))
    }

    @Test
    fun frontendOpensLoginPanelIfUserHasLoggedOut() {
        // GIVEN The user has logged in.
        runBlocking {
            accountServiceApi.coLogIn("username", SensitiveString("password"))
        }
        // AND The frontend is invoked from the menu.
        tapAccountMenuItem()
        // AND The info view is open.
        waitForView(thatIsAccountInfoPanel).check(matches(isDisplayed()))

        // WHEN The user has logged out.
        runBlocking { accountServiceApi.coLogOut() }

        // THEN The login view is open.
        waitForView(thatIsAccountLoginView).check(matches(isDisplayed()))

        // WHEN The frontend is closed.
        tapAccountMenuItem()
        // AND The frontend is open again.
        tapAccountMenuItem()

        // THEN The login view is open.
        waitForView(thatIsAccountLoginView).check(matches(isDisplayed()))
    }

    private fun tapAccountMenuItem() = withIviServiceMockOnTestThread(MenuServiceMock::class) {
        injectMenuItemClicked(accountMenuItem)
    }

    companion object {
        private val thatIsAccountLoginView = withIdReference(R.id::frontend_account_login_view)
        private val thatIsAccountInfoPanel = withIdReference(R.id::frontend_account_info_view)
    }
}