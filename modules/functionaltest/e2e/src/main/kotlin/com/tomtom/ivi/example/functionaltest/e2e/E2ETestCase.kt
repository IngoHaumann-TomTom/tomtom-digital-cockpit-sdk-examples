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

package com.tomtom.ivi.example.functionaltest.e2e

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.tomtom.ivi.api.framework.iviinstance.IviInstanceId
import com.tomtom.ivi.tools.testing.functional.CreateApiCallback
import com.tomtom.ivi.tools.testing.functional.GetServiceIdsCallback
import com.tomtom.ivi.tools.testing.functional.IviActivityTestCase
import com.tomtom.ivi.tools.testing.functional.IviE2eTestCase
import com.tomtom.ivi.tools.testing.functional.WebserviceMockRuleConfig
import com.tomtom.ivi.tools.testing.mainmenufrontend.waitForMainMenuIsDisplayed
import com.tomtom.ivi.tools.testing.navigationfunctional.NavkitConfigurationMockRule
import com.tomtom.ivi.tools.testing.navigationfunctional.createWebserviceMockRuleConfig
import com.tomtom.ivi.tools.testing.systemui.SystemUiIdlingResourceRule
import com.tomtom.ivi.tools.testing.systemui.assertCoreContentsAreDisplayed
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
        get() = createWebserviceMockRuleConfig(
            isWebserviceCaptureRun,
            shouldIncludeNavigation = true,
            shouldIncludeMapDisplay = true,
            shouldIncludeTrafficIncidents = true
        )

    override val preActivityRule: RuleChain = RuleChain
        .outerRule(NavkitConfigurationMockRule(webserviceMockUri))

    @Before
    fun waitForMainMenu() = waitForMainMenuIsDisplayed(timeoutMs = DEFAULT_WAIT_MAIN_MENU_TIMEOUT)

    @Before
    fun checkCoreComponentsAreDisplayed() = assertCoreContentsAreDisplayed()

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
