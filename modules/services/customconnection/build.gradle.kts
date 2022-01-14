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

import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    /**
     * TODO(IVI-2177): The public interface of the unit testing classes are not yet stable.
     *   Therefore, this annotation is needed in this and customer projects. Before the mentioned
     *   ticket is closed, check if this is still needed, or change the mentioned ticket reference
     *   to the relevant ticket which relates to making the unit testing classes stable.
     */
    optInToExperimentalApis = true
}

dependencies {
    implementation(libraries.androidxLifecycleService)
    implementation(libraries.indigoPlatformTelecomApiServiceTelecom)

    testImplementation(libraries.indigoPlatformToolsApiTestingUnit)
}
