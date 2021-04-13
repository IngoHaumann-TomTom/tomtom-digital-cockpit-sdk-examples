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

package com.tomtom.ivi.example.functionaltest.integration

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tomtom.ivi.core.serviceapi.menu.MenuServiceMock
import com.tomtom.ivi.example.frontend.account.accountFrontendMetadata
import com.tomtom.ivi.example.serviceapi.account.AccountService
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

class AccountFrontendTest : FrontendTestCase(
    testFrontendMetadataConfig = defaultTestFrontendMetadataConfig + accountFrontendMetadata
) {
    private val accountServiceApi by lazy {
        // API wrapper cannot be created before test environment is fully configured, hence create
        // a wrapper lazily.
        createApiWrapper { lifecycleOwner, iviServiceProvider ->
            AccountService.createApi(lifecycleOwner, iviServiceProvider)
        }
    }

    @Before
    fun waitForAccountService() {
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
        runBlocking { accountServiceApi.coLogIn("username", "password") }

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
        runBlocking { accountServiceApi.coLogIn("username", "password") }
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
        injectMenuItemClicked(accountFrontendMetadata.launchMenuItem)
    }

    companion object {
        private val thatIsAccountLoginView = withIdReference(R.id::frontend_account_login_view)
        private val thatIsAccountInfoPanel = withIdReference(R.id::frontend_account_info_view)
    }
}