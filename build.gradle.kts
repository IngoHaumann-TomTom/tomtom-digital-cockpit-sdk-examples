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
import com.tomtom.ivi.buildsrc.environment.Versions
import com.tomtom.ivi.buildsrc.extensions.android
import com.tomtom.ivi.buildsrc.extensions.getGradleProperty
import com.tomtom.ivi.buildsrc.extensions.kotlinOptions
import com.tomtom.ivi.gradle.api.common.dependencies.IviDependencySource
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

apply(from = rootProject.file("buildSrc/tasks/indigoPlatformUpdate.gradle.kts"))
// TODO(IVI-2890): Use the Gradle plugin.
apply(from = rootProject.file("buildSrc/tasks/buildVersion.gradle.kts"))

plugins {
    `kotlin-dsl`
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("com.tomtom.ivi.platform") apply true
    id("com.tomtom.ivi.defaults.core") apply true
}

apply(from = rootProject.file("buildSrc/repositories.gradle.kts"))

ivi {
    dependencySource = IviDependencySource.Artifactory(Versions.INDIGO_PLATFORM)
}

// Set up the git version for Android and CI
val buildVersions = com.tomtom.ivi.buildsrc.environment.BuildVersioning(rootProject)
val versionCode: Int by extra(buildVersions.versionCode)
val versionName: String by extra(buildVersions.versionName)

subprojects {
    val isApplicationProject by extra(getGradleProperty("isApplicationProject", false))

    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlin" &&
                    requested.name in listOf("kotlin-reflect", "kotlin-stdlib-jdk8")
                ) {
                    useVersion("1.4.0")
                }
            }
        }
    }

    if (isApplicationProject) {
        apply(plugin = "com.android.application")
    } else {
        apply(plugin = "com.android.library")
    }
    apply(plugin = "kotlin-android")
    apply(plugin = "kotlin-parcelize")

    apply(from = rootProject.file("buildSrc/repositories.gradle.kts"))
    apply(from = rootProject.file("buildSrc/tasks/publish.gradle.kts"))

    //TODO(IVI-2756): Remove OptIn when service registration is internal.
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    dependencies {
        implementation(Libraries.Android.ANNOTATION)
        implementation(Libraries.Android.KTX)
    }

    android {
        compileSdkVersion(Versions.COMPILE_SDK)
        buildToolsVersion = Versions.BUILD_TOOLS

        defaultConfig {
            minSdkVersion(Versions.MIN_SDK)
            targetSdkVersion(Versions.TARGET_SDK)
            if (isApplicationProject) {
                versionCode = rootProject.extra.get("versionCode") as Int
                versionName = rootProject.extra.get("versionName") as String
            }
        }

        compileOptions {
            sourceCompatibility = Versions.JAVA_COMPATIBILITY
            targetCompatibility = Versions.JAVA_COMPATIBILITY
        }

        kotlinOptions {
            @Suppress("UnstableApiUsage")
            jvmTarget = Versions.JVM
        }

        lintOptions {
            // lintConfig = File("lint.xml")
            isAbortOnError = false
            isCheckAllWarnings = true
            isCheckDependencies = false
            isWarningsAsErrors = true
            xmlOutput = File(buildDir, "reports/lint/report.xml")
            htmlOutput = File(buildDir, "reports/lint/report.html")

            disable(
                // New dependency version checking is done during development.
                "GradleDependency",
                "NewerVersionAvailable",
                // Use of synthetic accessors is accepted, assuming we'll be using multidexing.
                "SyntheticAccessor",
                // The product does require protected system permissions.
                "ProtectedPermissions",
                // Accessibility text is not useful in this product.
                "ContentDescription",
                // We don't need to be indexable by Google Search.
                "GoogleAppIndexingApiWarning",
                // 'supportsRtl' is defined in the product manifest.
                "RtlEnabled",
                // Ignore duplicate strings, which are most likely due to using the same
                // string in different contexts.
                "DuplicateStrings",
                // Do not check for style-related properties, as they're defined as attributes
                // and used in the styles, and thus depend on dependencies to be verified.
                // The main product module does enable these checks.
                "RequiredSize",
                "UnusedResources"
            )
        }

        packagingOptions {
            // For NavKit 2, pick the first binary found when there are multiple.
            pickFirst("lib/**/*.so")
            // NOTE: Do not strip any binaries: they should already come stripped from the
            // release artifacts; and since we don't use an NDK, they cannot be stripped anyway.
            doNotStrip("*.so")
        }

        // Split the output into multiple APKs based on their ABI.
        splits.abi {
            @Suppress("UnstableApiUsage")
            isEnable = true
            include(
                com.android.sdklib.devices.Abi.ARM64_V8A.toString(),
                com.android.sdklib.devices.Abi.X86_64.toString()
            )
        }

        val projectSourceSets by extra(mutableSetOf<String>())
        sourceSets.all {
            val path = "src/$name/kotlin"
            java.srcDir(path)
            File(projectDir, path).takeIf { it.exists() }?.let {
                projectSourceSets += it.absolutePath
            }
        }
    }
}
