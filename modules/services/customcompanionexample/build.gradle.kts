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

import com.tomtom.ivi.buildsrc.environment.Libraries

// TODO(IVI-4703): Remove `RequiresOptIn` when `Libraries.TomTom.CommunicationsSdk.CLIENT`
//   allows it.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.RequiresOptIn"
    )
}

dependencies {
    implementation(project(":serviceapis_companionexample"))
    implementation(Libraries.TomTom.CommunicationsSdk.CLIENT)
    implementation(Libraries.TomTom.CommunicationsSdk.COMMON)
    implementation(Libraries.TomTom.CommunicationsSdk.EXAMPLESERVICE)
    testImplementation(Libraries.TomTom.Indigo.TOOLS_TESTING_UNIT)
}
