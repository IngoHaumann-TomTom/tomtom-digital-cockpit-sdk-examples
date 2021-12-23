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

package com.tomtom.ivi.example.functionaltest.e2e

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.tomtom.ivi.platform.framework.api.common.iviinstance.IviInstanceId
import com.tomtom.ivi.tools.testing.functional.CreateApiCallback
import com.tomtom.ivi.tools.testing.functional.GetServiceIdsCallback
import com.tomtom.ivi.tools.testing.functional.IviActivityTestCase
import com.tomtom.ivi.tools.testing.functional.IviE2eTestCase
import com.tomtom.ivi.tools.testing.functional.WebserviceMockRuleConfig
import com.tomtom.ivi.tools.testing.mainmenufrontend.MainMenuButton
import com.tomtom.ivi.tools.testing.mainmenufrontend.waitForMainMenuButtonIsDisplayed
import com.tomtom.ivi.tools.testing.navigationfunctional.NavkitConfigurationMockRule
import com.tomtom.ivi.tools.testing.navigationfunctional.createWebserviceMockRuleConfig
import com.tomtom.ivi.platform.systemui.api.testing.tools.SystemUiIdlingResourceRule
import com.tomtom.ivi.platform.systemui.api.testing.tools.assertCoreContentsAreDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

/**
 * A base class for E2E tests of the example product that provides common functionality,
 * such as accessing frequently used containers or opening the debug panel.
 */
abstract class E2ETestCase :
    IviActivityTestCase(IviIntent("com.tomtom.ivi.example.product.exampleapp.MAIN")),
    IviE2eTestCase {

    /**
     * We cannot use [Intent] due to bug in [ActivityScenarioRule],
     * [see issue](https://github.com/android/android-test/issues/496).
     * As workaround, we loose filtering to an action check so the intent is matched by the rule.
     */
    private class IviIntent(action: String) : Intent(action) {
        override fun filterEquals(other: Intent?): Boolean =
            action == other?.action
    }

    @get:Rule
    val systemUiIdlingResourceRule = SystemUiIdlingResourceRule(activityRule)

    override val initialWebserviceMockRuleConfig: WebserviceMockRuleConfig
        get() = createWebserviceMockRuleConfig(isWebserviceCaptureRun) {
            includeNavigationWithAllowedMax10FailedRequests()
            includeMapDisplay()
            includeTrafficIncidents()
        }

    override val preActivityRule: RuleChain = RuleChain
        .outerRule(NavkitConfigurationMockRule(webserviceMockUri))

    @Before
    fun waitForMainMenuIsDisplayed() {
        // Wait for system UI to show container for main menu and for home.
        assertCoreContentsAreDisplayed()

        // Wait for main menu frontend to display the buttons.
        waitForMainMenuButtonIsDisplayed(
            MainMenuButton.NAVIGATION,
            timeoutMs = DEFAULT_WAIT_MAIN_MENU_TIMEOUT
        )
    }

    override fun <R> createApiWrapper(
        iviInstanceId: IviInstanceId,
        callback: CreateApiCallback<R>
    ) =
        createApiWrapperInternal(iviInstanceId, callback)

    override fun getServiceIdsWrapper(
        iviInstanceId: IviInstanceId,
        callback: GetServiceIdsCallback
    ) =
        getServiceIdsWrapperInternal(iviInstanceId, callback)

    companion object {
        /**
         * This is an insanely long timeout. Typically needed for the second running test when
         * running on the emulator. This is because the `lowmemorykiller` is having a party (aka
         * killing all kinds of processes) triggered by the start of the main activity for the
         * first time (which requires a lot of memory) by the first test. The party continues into
         * the second test. In between the first test and the second test the `lowmemorykiller` even
         * kills our service processes (rightfully, as the processes are low hanging fruit at this
         * time), making matters worse for the second test to restart the processes. The first
         * startup of the activity also triggers download of maps etc, so making IndiGo even more
         * hungry for memory.
         * Once the party dies out, all is fine and our service processes keep running in between
         * tests and they lived happily ever after. Or so they say.
         */
        private const val DEFAULT_WAIT_MAIN_MENU_TIMEOUT = 20_000L
    }
}
