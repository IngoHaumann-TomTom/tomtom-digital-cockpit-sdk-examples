/*
 * Copyright © 2022 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.example.ivi.example.media.miniplayer

import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaConfiguration
import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaPolicyFrontendExtension
import com.tomtom.ivi.appsuite.media.api.common.frontend.asMediaConfiguration
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.MediaTaskPanelPolicy
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.PolicyProvider
import com.tomtom.ivi.platform.frontend.api.common.frontend.Frontend
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendBuilder
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendContext

/**
 * This [FrontendBuilder] uses the [MediaPolicyFrontendExtension]s from the IVI Gradle configuration
 * of the media frontend to customize the media sources exactly as the stock media player UI does.
 *
 * It is convenient when it is only necessary to create a new visual look for the "currently
 * playing media" panel, or when moving it to a different part of the TomTom IndiGO system UI.
 *
 * For any deeper levels of customization, such as making a whole new media player UI, it is
 * necessary to carefully evaluate what to re-use from the stock [MediaConfiguration]. For
 * instance, the [MediaTaskPanelPolicy] provided by a [PolicyProvider] is probably not reusable
 * in that case.
 */
internal class ExampleMiniPlayerFrontendBuilder : FrontendBuilder() {

    override fun build(frontendContext: FrontendContext): Frontend {
        val extensions = getFrontendExtensionsByType<MediaPolicyFrontendExtension>()
        return ExampleMiniPlayerFrontend(
            frontendContext,
            extensions.asMediaConfiguration()
        )
    }
}
