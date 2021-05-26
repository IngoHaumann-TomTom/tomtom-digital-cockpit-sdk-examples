package com.tomtom.ivi.example.functionaltest.e2e

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.tomtom.ivi.tools.testing.mainmenufrontend.MainMenuButton
import com.tomtom.ivi.tools.testing.mainmenufrontend.onMainMenuView
import com.tomtom.tools.android.testing.functional.waitForView
import com.tomtom.tools.android.testing.functional.withIdReference
import org.junit.Ignore
import org.junit.Test

class AccountFrontendTest : E2ETestCase() {
    @Test
    @Ignore("IVI-3916")
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