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

import com.android.builder.core.BuilderConstants
import com.tomtom.ivi.buildsrc.environment.Libraries
import com.tomtom.ivi.buildsrc.environment.Versions
import com.tomtom.ivi.buildsrc.extensions.android
import com.tomtom.ivi.buildsrc.extensions.getGradleProperty
import com.tomtom.ivi.buildsrc.extensions.kotlinOptions
import com.tomtom.ivi.platform.gradle.api.common.dependencies.IviDependencySource
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi
import com.tomtom.ivi.platform.gradle.api.tools.version.iviAndroidVersionCode
import com.tomtom.ivi.platform.gradle.api.tools.version.iviVersion
import com.tomtom.navtest.NavTestAndroidProjectExtension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

apply(from = rootProject.file("buildSrc/repositories.gradle.kts"))
apply(from = rootProject.file("buildSrc/tasks/installRepositoriesCfg.gradle.kts"))
apply(from = rootProject.file("buildSrc/tasks/setupEnv.gradle.kts"))
apply(from = rootProject.file("buildSrc/tasks/indigoPlatformUpdate.gradle.kts"))

plugins {
    `kotlin-dsl`
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("com.android.test") apply false
    id("com.tomtom.ivi.platform.framework.config") apply true
    id("com.tomtom.ivi.platform.tools.version") apply true
    id("com.tomtom.navtest") apply true
    id("com.tomtom.navui.emulators-plugin") apply false
}

// Make a single directory where to store all test results.
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
val testRootDir: File by extra(File(rootProject.projectDir, "IviTest"))
val testOutputDirectory: File by extra(testRootDir.resolve(LocalDateTime.now().format(formatter)))

// Configure Android emulator options.
apply(from = file("emulators.gradle.kts"))

ivi {
    dependencySource = IviDependencySource.ArtifactRepository(Versions.INDIGO_PLATFORM)
}

// Set up global test options
tasks.withType<Test> {
    testLogging {
        // Logging exceptions verbosely helps on CI to immediately see the source of testing
        // errors, especially in case of crashes.
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

// Set up the NavTest framework.
navTest {
    // Specify where the report and artifacts of the tests will be archived
    outputDir = testOutputDirectory

    deviceUsageReport {
        enabled = true
    }

    timeline {
        enabled = true
    }

    suites {
        create("unit") {
            includeTags += "unit"
        }
        create("integration") {
            includeTags += "integration"
        }
        create("e2e") {
            includeTags += "e2e"
        }
    }
}

subprojects {
    val isApplicationProject by extra(getGradleProperty("isApplicationProject", false))
    val isAndroidTestProject by extra(getGradleProperty("isAndroidTestProject", false))

    when {
        isApplicationProject -> apply(plugin = "com.android.application")
        isAndroidTestProject -> apply(plugin = "com.android.test")
        else -> apply(plugin = "com.android.library")
    }

    apply(plugin = "kotlin-android")
    apply(plugin = "kotlin-parcelize")

    apply(from = rootProject.file("buildSrc/repositories.gradle.kts"))
    apply(from = rootProject.file("buildSrc/tasks/publish.gradle.kts"))

    dependencies {
        // Enforces the same version for Kotlin libraries.
        implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom"))

        constraints {
            // kotlin-reflect dependency is not constrained up by kotlin-bom, so we need to
            // constrain it explicitly.
            implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}")
        }

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
                versionCode = iviAndroidVersionCode
                versionName = iviVersion
            }
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            pickFirst("META-INF/io.netty.versions.properties")
            exclude("META-INF/INDEX.LIST")
        }

        // Split the output into multiple APKs based on their ABI.
        splits.abi {
            @Suppress("UnstableApiUsage")
            isEnable = true
            include(
                com.android.sdklib.devices.Abi.ARM64_V8A.toString(),
                com.android.sdklib.devices.Abi.ARMEABI_V7A.toString(),
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

    apply(plugin = "com.tomtom.ivi.platform.tools.signing-config")

    apply(plugin = "com.tomtom.navtest")

    configure<NavTestAndroidProjectExtension> {
        // Applies for functional tests under "androidTest" directory.
        androidTest {
            enabled = true
            // Tags are set by subprojects.

            // Allow specifying the test-class via command-line
            findProperty("testClass")?.let {
                instrumentationArguments.className = it as String
            }
        }

        pluginManager.withPlugin("com.tomtom.ivi.platform.framework.config.activity-test") {
            androidTest {
                testTags += "integration"
                timeout = 10 * 60
            }
        }

        if (!isAndroidTestProject) {
            // Applies for unit tests under "test" directory.
            unit {
                enabled = true
                testTags += "unit"
                variantFilter = { it.buildType.name == BuilderConstants.DEBUG }
            }
        }
    }
}

tasks.register<Exec>("docs") {
    workingDir("${project.projectDir}/docs/portal")
    commandLine("${project.projectDir}/docs/portal/convert.sh")
    args("${project.projectDir}/docs/portal/html")
}
