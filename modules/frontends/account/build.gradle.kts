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

import com.tomtom.ivi.buildsrc.environment.Libraries

plugins {
    id("com.tomtom.ivi.platform.framework.frontend")
}

dependencies {
    implementation(project(":serviceapis_account"))

    implementation(Libraries.TomTom.Indigo.API_COMMON_LIFECYCLE)
    implementation(Libraries.TomTom.Tools.API_LIVEDATA)
}
