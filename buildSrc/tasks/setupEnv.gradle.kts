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

tasks.register("setupEnv") {
    dependsOn(":installRepositoriesCfg")
}

/*
 * This dirty, dirty hack disables a check done by the Android Gradle plugin which literally causes
 * hundreds of thousands of lines of stacktraces on builds launched with the `--info` and `--debug`
 * command line arguments. We seem to not have any build scripts responsible for the warnings
 * Google complains about: they all come from dependencies.
 *
 * The warning in question:
 * ```
 * java.lang.RuntimeException: Configuration '<whatever>' was resolved during configuration time.
 * This is a build performance and scalability issue.
 * See https://github.com/gradle/gradle/issues/2298
 * ```
 *
 * When updating the Android Gradle plugin, verify the state of the issue above, and if it's fixed
 * in the new version, remove this.
 */
project.extensions.add("_internalAndroidGradlePluginDependencyCheckerRegistered", true)
