/*
 * Copyright © 2021 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.example.functionaltest.e2e

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.tomtom.ivi.platform.mainmenu.api.testing.frontend.MainMenuButton
import com.tomtom.ivi.platform.mainmenu.api.testing.frontend.onMainMenuView
import com.tomtom.tools.android.testing.functional.waitForView
import com.tomtom.tools.android.testing.functional.withIdReference
import org.junit.Ignore
import org.junit.Test

internal class AccountFrontendTest : E2ETestCase() {
    @Test
    fun openAccountFrontendFromMainMenu() {
        onMainMenuView(MainMenuButton("Account"))
            .check(matches(isDisplayed()))
            .perform(click())

        waitForView(thatIsAccountLoginView).check(matches(isDisplayed()))
    }

    companion object {
        private val thatIsAccountLoginView = withIdReference(R.id::frontend_account_login_view)
    }

}
