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

import com.android.builder.core.BuilderConstants
import com.tomtom.ivi.buildsrc.environment.Libraries
import com.tomtom.ivi.buildsrc.environment.Versions
import com.tomtom.ivi.buildsrc.extensions.android
import com.tomtom.ivi.buildsrc.extensions.getGradleProperty
import com.tomtom.ivi.buildsrc.extensions.kotlinOptions
import com.tomtom.ivi.platform.gradle.api.common.dependencies.IviDependencySource
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi
import com.tomtom.ivi.platform.gradle.api.tools.version.iviAndroidVersionCode
import com.tomtom.ivi.platform.gradle.api.tools.emulators.iviEmulators
import com.tomtom.ivi.platform.gradle.api.tools.version.iviVersion
import com.tomtom.navtest.android.android
import com.tomtom.navtest.android.androidRoot
import com.tomtom.navtest.extensions.navTest
import com.tomtom.navtest.extensions.navTestRoot
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
    id("com.tomtom.ivi.platform.tools.emulators") apply true
    id("com.tomtom.ivi.platform.tools.version") apply true
    id("com.tomtom.navtest") apply true
    id("com.tomtom.navtest.android") apply true
    id("com.tomtom.navui.emulators-plugin") apply false
}

// Make a single directory where to store all test results.
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
val testRootDir: File by extra(File(rootProject.projectDir, "IviTest"))
val testOutputDirectory: File by extra(testRootDir.resolve(LocalDateTime.now().format(formatter)))

ivi {
    dependencySource = IviDependencySource.ArtifactRepository(Versions.INDIGO_PLATFORM)
}

iviEmulators {
    findProperty("emulatorsDirectory")?.toString()?.let {
        emulatorsDirectory = File(it)
    }
    findProperty("multiDisplay")?.toString()?.toBoolean()?.let {
        enableMultiDisplay = it
    }
    findProperty("numberOfEmulators")?.toString()?.toInt()?.let {
        numberOfInstances = it
    }
    findProperty("emulatorImage")?.let {
        emulatorImage = it.toString()
    }
    minApiLevel = Versions.MIN_SDK
    outputDirectory = testOutputDirectory
    targetApiLevel = Versions.COMPILE_SDK
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
navTestRoot {
    // Specify where the report and artifacts of the tests will be archived
    outputDir.set(testOutputDirectory)

    androidRoot {
        deviceUsageReport {
            enabled.set(true)
        }
    }

    timeline {
        enabled.set(true)
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
            minSdk = Versions.MIN_SDK
            targetSdk = Versions.TARGET_SDK
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
            pickFirsts.add("lib/**/*.so")
            // NOTE: Do not strip any binaries: they should already come stripped from the
            // release artifacts; and since we don't use an NDK, they cannot be stripped anyway.
            jniLibs.keepDebugSymbols.add("*.so")
            pickFirsts.add("META-INF/io.netty.versions.properties")
            resources.excludes.add("META-INF/INDEX.LIST")
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


        apply(plugin = "com.tomtom.ivi.platform.tools.signing-config")

        apply(plugin = "com.tomtom.navtest")
        apply(plugin = "com.tomtom.navtest.android")

        navTest.android {
            // Applies for functional tests under "androidTest" directory.
            androidTest {
                enabled.set(true)
                // Tags are set by subprojects.

                // Allow specifying the test-class via command-line
                findProperty("testClass")?.let {
                    instrumentationArguments.className.set(it as String)
                }
            }

            pluginManager.withPlugin("com.tomtom.ivi.platform.framework.config.activity-test") {
                androidTest {
                    testTags.add("integration")
                    timeout.set(10 * 60)
                }
            }

            if (!isAndroidTestProject) {
                // Applies for unit tests under "test" directory.
                unit {
                    enabled.set(true)
                    testTags.add("unit")
                    variantFilter = { it.buildType == BuilderConstants.DEBUG }
                }
            }
        }
    }
}

// TODO(IVI-4959): Remove the old 'docs' task.
tasks.register<Exec>("docs") {
    val portalDirectory = "${project.projectDir}/docs/portal"
    val outputDirectory = portalDirectory + "/html"
    description = "Generate html documentation in '$outputDirectory'"
    group = "Documentation"

    workingDir(portalDirectory)
    commandLine(portalDirectory + "/convert.sh")
    args(outputDirectory)
}

// TODO(IVI-4959): Rename this task to 'docs'.
/**
 * This task requires you to have Python3 and Ruby packages installed on your system.
 * For installing the Ruby packages, consult the README.md under docs/portal/site.
 */
tasks.register<Exec>("portal_export") {
    val portalDirectory = "${project.projectDir}/docs/portal/site"
    description = "Generates Developer Portal content for export."
    group = "Documentation"

    workingDir(portalDirectory)
    commandLine("python3")
    args("-B", "scripts/portal_generator.py", "export")
}

tasks.register<Exec>("portal_check") {
    val portalDirectory = "${project.projectDir}/docs/portal/site"
    description = "Validates Developer Portal content."
    group = "Documentation"

    workingDir(portalDirectory)
    commandLine("python3")
    args("-B", "scripts/portal_generator.py")
}
