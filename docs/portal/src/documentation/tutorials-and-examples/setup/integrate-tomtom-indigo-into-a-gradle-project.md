---
title: Integrate TomTom IndiGO into a Gradle Project
---

This pages contains the steps required to integrate the TomTom IndiGO platform into an existing
Android Gradle project. If you are new to TomTom IndiGO, our recommendation is to use the examples
as a starting point as these examples already cover the steps described on this page. Use the steps
on this page as a reference to integrate the TomTom IndiGO platform into an existing / newly created
Android Gradle project. These steps assume a basic level of experience with setting up Gradle build
files using Kotlin.

## Maven repositories

Gradle will need to be able to download TomTom IndiGO platform dependencies from TomTom's Maven
repositories for which login credentials are required. These can be obtained from TomTom.

Access to these repositories can be configured in Gradle as follows:

```kotlin
pluginManagement {
    repositories {
        // Local artifact cache.
        mavenLocal()

        // TomTom IndiGO's Nexus repository.
        maven("https://repo.tomtom.com/repository/ivi") {
            credentials {
                username =
                    if (extra.has("nexusUsername")) extra["nexusUsername"].toString() else ""
                password =
                    if (extra.has("nexusPassword")) extra["nexusPassword"].toString() else ""
            }
        }

        // External repositories.
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
    }
}

dependencyResolutionManagement {
    repositories {
        // Local artifact cache.
        mavenLocal()

        // TomTom IndiGO's Nexus repository.
        maven("https://repo.tomtom.com/repository/ivi") {
            credentials {
                username =
                    if (extra.has("nexusUsername")) extra["nexusUsername"].toString() else ""
                password =
                    if (extra.has("nexusPassword")) extra["nexusPassword"].toString() else ""
            }
        }

        // TomTom's Nexus repository for the Connectivity Agent.
        maven("https://maven.tomtom.com:8443/nexus/content/repositories/releases/")

        // External repositories.
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
    }
}
```

The above needs to be applied to `buildscript`, `buildSrc` and to all projects. As such, place the
above in a file called `build-logic/repositories.gradle.kts` and apply this file in
the top-level `settings.gradle.kts` file like:

```kotlin
apply(from = "build-logic/repositories.gradle.kts")
```

and in your `buildSrc/settings.gradle.kts` file like:

```kotlin
apply(from = "../build-logic/repositories.gradle.kts")
```

## Dependency management

The TomTom IndiGO platform publishes a version catalog which you can use to ensure your product
uses the same versions as used by the TomTom IndiGO platform. Create:
`build-logic/indigodependencies.versioncatalog.gradle.kts` and add:

```kotlin
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("indigoDependencies") {
            val group = "com.tomtom.ivi.platform"
            val artifact = "dependencies-catalog"
            val version = "<TOMTOM-INDIGO-VERSION>"
            from("${group}:${artifact}:${version}")
        }
    }
}
```

Replace the `<TOMTOM-INDIGO-VERSION>` with the TomTom IndiGO version you want to use.

Next apply this file in the top-level `settings.gradle.kts` file and `buildSrc/settings.gradle.kts`
files.

## BuildSrc dependencies

The TomTom IndiGO platform provides Gradle plugins for the build-time configuration of the TomTom
IndiGO platform. This allows you to, for example, include all of IndiGO's default frontends in your
product. To allow the Gradle plugins to be used in the Gradle projects it is required to add the
Gradle plugins as `implementation` dependencies to the `buildSrc`. The following adds these
dependencies:

**buildSrc/build.gradle.kts**

```kotlin
dependencies {
    val indigoPlatformVersion = ...

    // Optional: Plugin to configure the NavKit2 API key at build-time.
    implementation("com.tomtom.ivi.appsuite.gradle.navkit2:api_appsuitedefaults_navkit2:$indigoPlatformVersion")

    // Mandatory: Plugin to configure the IVI application at build-time.
    implementation("com.tomtom.ivi.platform.gradle:api_framework_config:$indigoPlatformVersion")

    // Optional: Plugin for versioning the APK based on the Git repository information.
    implementation("com.tomtom.ivi.platform.gradle:api_tools_version:$indigoPlatformVersion")

    // Optional: Plugin to use the default frontends and services from the TomTom IndiGO platform
    // and app suite.
    implementation("com.tomtom.ivi.product.gradle:api_productdefaults_core:$indigoPlatformVersion")

    // Mandatory: For IVI services plugins.
    implementation(indigoDependencies.gradlePluginKsp)
}
```

## Root project configuration

Apply the following plugins in the top-level `build.gradle.kts` file to the root Gradle project:

```kotlin
plugins {
    `kotlin-dsl`
    ...
    // Mandatory: For IVI service plugins.
    id("com.google.devtools.ksp") apply false
    // Mandatory: For configure the IVI application at build-time.
    id("com.tomtom.ivi.platform.framework.config")
    // Optional: Plugin for versioning the APK based on the Git repository information.
    id("com.tomtom.ivi.platform.tools.version")
}
```

In the same file, configure the TomTom IndiGO platform dependency source:

```kotlin
ivi {
    dependencySource = IviDependencySource.Artifactory(Versions.INDIGO_PLATFORM)
}
```

## Integrating TomTom IndiGO platform into the APK

To integrate the TomTom IndiGO platform into an APK, you can add the following to the
`build.gradle.kts` of the project that builds the APK:

```kotlin
plugins {
    // Optional: To use the default frontends and services from the TomTom IndiGO platform and app
    // suite.
    id("com.tomtom.ivi.product.defaults.core")

    // Optional: To configure the NavKit2 API key at build-time.
    id("com.tomtom.ivi.appsuite.navkit2.defaults.config")
}

ivi {
    application {
        enabled = true
    }
}
```

### Customization of TomTom IndiGO platform and appsuite default frontends and services

The Gradle plugin applied in the example above `id("com.tomtom.ivi.product.defaults.core")`
enables all the default frontends, frontend extensions, menu items and IVI service hosts from the
TomTom IndiGO platform and app suite for default runtime deployment.

If you only want to apply defaults from the TomTom IndiGO platform without the appsuite default, you
can achieve this by only applying the `id("com.tomtom.ivi.platform.defaults.core")` Gradle plugin.
Additionally, if you want to include a selection of the TomTom IndiGO Applications, you can apply
the Gradle plugins for each individual TomTom IndiGO Application.

To allow these Gradle plugins to be used in the Gradle projects it is required to add the Gradle
plugins as `implementation` dependencies to the `buildSrc`. The following adds these dependencies:

```kotlin
dependencies {
    val indigoPlatformVersion = ...

    // Optional: Plugin to configure in the default frontends and services from the TomTom IndiGO platform.
    implementation("com.tomtom.ivi.platform.gradle:api_defaults_core:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from NavKit2 TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.navkit2:api_appsuitedefaults_navkit2:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Media TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.media:api_appsuitedefaults_media:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Communications TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.communications:api_appsuitedefaults_communications:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from User Profiles TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.userprofiles:api_appsuitedefaults_userprofiles:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Vehicle Settings TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.vehiclesettings:api_appsuitedefaults_vehiclesettings:$indigoPlatformVersion")

    // Optional: Plugin to configure in the defaults from App Store TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.appstore:api_appsuitedefaults_appstore:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Bluetooth TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.bluetooth:api_appsuitedefaults_bluetooth:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Companion TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.companionapp:api_appsuitedefaults_companionapp:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Hvac TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.hvac:api_appsuitedefaults_hvac:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Messaging TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.messaging:api_appsuitedefaults_messaging:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Navigation TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.navigation:api_appsuitedefaults_navigation:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from System Status TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.systemstatus:api_appsuitedefaults_systemstatus2:$indigoPlatformVersion")
    // Optional: Plugin to configure in the defaults from Vpa TomTom IndiGO Application.
    implementation("com.tomtom.ivi.appsuite.gradle.vpa:api_appsuitedefaults_vpa:$indigoPlatformVersion")
}
```

And then, apply necessary plugins in the `build.gradle.kts` of the project that builds the APK:

```kotlin
plugins {
    // Optional: To use the default frontends and services from the TomTom IndiGO platform only.
    // This plugin should be always applied first, before the rest of the `defaults` plugins.
    id("com.tomtom.ivi.platform.defaults.core")

    // Optional: To configure in the defaults from NavKit2 TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.navkit2.defaults.navkit2")
    // Optional: To configure in the defaults from Media TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.media.defaults.media")
    // Optional: To configure in the defaults from Communications TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.communications.defaults.communications")
    // Optional: To configure in the defaults from User Profiles TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.userprofiles.defaults.userprofiles")
    // Optional: To configure in the defaults from Vehicle Settings TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.vehiclesettings.defaults.vehiclesettings")

    // Optional: To configure in the defaults from App Store TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.appstore.defaults.appstore")
    // Optional: To configure in the defaults from Bluetooth TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.bluetooth.defaults.bluetooth")
    // Optional: To configure in the defaults from Companion App TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.companionapp.defaults.companionapp")
    // Optional: To configure in the defaults from Hvac TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.hvac.defaults.hvac")
    // Optional: To configure in the defaults from Messaging TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.messaging.defaults.messaging")
    // Optional: To configure in the defaults from Navigation TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.navigation.defaults.navigation")
    // Optional: To configure in the defaults from System Status TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.systemstatus.defaults.systemstatus")
    // Optional: To configure in the defaults from Vpa TomTom IndiGO Application.
    id("com.tomtom.ivi.appsuite.vpa.defaults.vpa")
}
```

Some of these Gradle plugins add menu items. The order in which these plugins are applied defines
the order of menu items within the main menu frontend.

## Module references

Before adding frontends or IVI service hosts, our recommendation is to implement the
`ModuleReference` class.

The `ModuleReference` implementation class can be used in the build configurations of the frontend
and IVI service host later on. It will be used to refer to modules that implement the frontend or
IVI service host and to resolve the full-qualified package names as well.

Create the `ModuleReference` implementation class (`<ProjectName>ModuleReference`) in `buildSrc`.
You can use `ExampleModuleReference.kt` in
`buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/dependencies/` as an example.

