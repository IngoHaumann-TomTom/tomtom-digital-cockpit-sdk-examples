---
title: Integrate TomTom IndiGO into a Gradle Project
---

<Blockquote>
    When using TomTom IndiGO version 1.0.3880-1630 or earlier, please skip this page and instead go
    to&nbsp;
    <a href="/tomtom-indigo/documentation/tutorials-and-examples/setup/integrate-tomtom-indigo-into-a-gradle-project-navkit2">
        Integrate TomTom IndiGO into a Gradle Project (NavKit2)
    </a>
    .
</Blockquote>

This pages contains the steps required to integrate the TomTom IndiGO platform into an existing
Android Gradle project. If you are new to TomTom IndiGO, our recommendation is to use the examples
as a starting point, as these already cover the steps described on this page. Use the steps on this
page as a reference to integrate the TomTom IndiGO platform into an existing / newly created Android
Gradle project. These steps assume a basic level of experience with setting up Gradle build files
using Kotlin.

## Maven repositories

Gradle will need to be able to download TomTom IndiGO platform dependencies from TomTom's Maven
repositories, for which login credentials are required. These can be obtained from TomTom.

Access to these repositories can be configured in Gradle as follows:

[`build-logic/repositories.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build-logic/repositories.gradle.kts#L12-L57)

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

The above needs to be applied to `buildscript`, `buildSrc` and to all projects. As such, apply this
file in the Gradle settings files.

[`settings.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/settings.gradle.kts#L14)

```kotlin
apply(from = "build-logic/repositories.gradle.kts")
```

[`buildSrc/settings.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/buildSrc/settings.gradle.kts#L12)

```kotlin
apply(from = "../build-logic/repositories.gradle.kts")
```

## Dependency management

The TomTom IndiGO platform publishes a version catalog which you can use to ensure your product
uses the same versions as used by the TomTom IndiGO platform.

[`build-logic/indigodependencies.versioncatalog.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build-logic/indigodependencies.versioncatalog.gradle.kts#L30-L40)

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

Replace the `<TOMTOM-INDIGO-VERSION>` with the TomTom IndiGO version you want to use, in format
`x.y.z`.

Next, apply this file in the top-level `settings.gradle.kts` file and `buildSrc/settings.gradle.kts`
files.

## BuildSrc dependencies

The TomTom IndiGO platform provides Gradle plugins for the build-time configuration of the TomTom
IndiGO platform. This allows you to, for example, include all of IndiGO's default frontends in your
product. To allow these Gradle plugins to be used in the Gradle projects, it is required to add them
as `implementation` dependencies to the `buildSrc`. The following adds these dependencies:

[`buildSrc/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/buildSrc/build.gradle.kts#L32-L37)

```kotlin
dependencies {
    val indigoPlatformVersion = "<TOMTOM-INDIGO-VERSION>"

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

Apply the following plugins to the root Gradle project:

[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build.gradle.kts#L36-L39)

```kotlin
plugins {
    `kotlin-dsl`
    ...
    // Mandatory: For IVI service plugins.
    id("com.google.devtools.ksp") apply false

    // Mandatory: For configuring the IVI application at build-time.
    id("com.tomtom.ivi.platform.framework.config")

    // Optional: Plugin for versioning the APK based on the Git repository information.
    id("com.tomtom.ivi.platform.tools.version")
}
```

In the same file, configure the TomTom IndiGO platform dependency source.

[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build.gradle.kts#L71-L74)

```kotlin
ivi {
    dependencySource = IviDependencySource.Artifactory(Versions.IVI_PLATFORM)
}
```

## Integrating TomTom IndiGO platform into the APK

To integrate the TomTom IndiGO platform into an APK, you can add the following to the
build file of the project that builds the APK:

[`my_project/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/alexa/app/build.gradle.kts#L18-L22)

```kotlin
plugins {
    // Optional: To use the default frontends and services from the TomTom IndiGO platform
    // and app suite.
    id("com.tomtom.ivi.product.defaults.core")
    // Optional: Add a non-default frontend.
    id("com.tomtom.ivi.appsuite.alexa.defaults.alexa")
}

ivi {
    application {
        enabled = true
    }
}
```

The Gradle plugin applied in this example `id("com.tomtom.ivi.product.defaults.core")`
configures all the default frontends, frontend extensions, menu items and IVI service hosts from the
TomTom IndiGO platform and app suite for the default runtime deployment. Unless defined otherwise
all default components are enabled in the `ivi` `application`.

If you only want to apply defaults from the TomTom IndiGO platform without the appsuite default, you
can achieve this by only applying the platform Gradle
plugin: `id("com.tomtom.ivi.platform.defaults.core")`.

Furthermore, you can add specific non-default platform or appsuite plugins, such
as `id("com.tomtom.ivi.appsuite.alexa.defaults.alexa")`. However, any non-default plugin components
need to be enabled explicitly in the `ivi` `application`.

### Configuring the IVI application

The `ivi` `application` contains three important blocks. The `iviInstances`, the `services` and
the `runtime`. If any of these blocks are not specified, defaults will apply. However, by using the
blocks, one can have control over how the platform is integrated.

This can be achieved in a fine-grained way by specifically adding, removing or replacing components
such as frontends, or in a more coarse-grained way by including or excluding groups.

[`my_project/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/alexa/app/build.gradle.kts#L31-L55)

```kotlin
ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                applyGroups {
                    includeDefaultGroups() // include all defaults.
                    include(IviAppsuite.alexaGroup) // include explicit opt-in app suite plugin.
                    exclude(IviPlatform.debugGroup) // exclude the default debug group.
                }
                frontends {
                    replace(userProfileFrontend, accountFrontend)
                    remove(mediaFrontend)
                }
                menuItems {
                    add(myMenuItem)
                    replace(userProfileMenuItem, accountMenuItem to accountFrontend)
                }
            }
        }
        services {
            applyGroups {
                includeDefaultGroups()
                include(IviAppsuite.alexaGroup)
                exclude(IviPlatform.debugGroup)
            }
        }
    }
}
```

Applying groups can be particularly useful for long term stable integration. For instance, in the
above example, the `alexaGroup` is an opt-in group. Instead of adding the specific frontend and menu
items specifically, the entire group is added. This is robust against future changes, which may add
or remove frontends or menu items. If the fine-grained method is used, it would require changes to
the `build.gradle.kts`, but using groups this is not needed.

Similarly, if a default group needs to be excluded, this can be achieved by excluding all components
individually (frontends, menu items, services), or simply by excluding its group. The example below
excludes the `debugGroup` for the release build only.

```kotlin
androidComponents {
    onVariants { variant ->
        variant.ivi {
            application {
                enabled = true
                iviInstances {
                    create(IviInstanceIdentifier.default) {
                        applyGroups {
                            includeDefaultGroups() // include all defaults.
                            if (variant.buildType == "release") {
                                exclude(IviPlatform.debugGroup) // exclude the default debug group.
                            }
                        }
                    }
                }
                services {
                    applyGroups {
                        includeDefaultGroups()
                        if (variant.buildType == "release") {
                            exclude(IviPlatform.debugGroup) // exclude the default debug group.
                        }
                    }
                }
            }
        }
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
the Gradle plugin for each individual TomTom IndiGO Application.

To allow these Gradle plugins to be used in the Gradle projects, it is required to add them as
`implementation` dependencies to the `buildSrc`. The following adds these dependencies:

```kotlin
dependencies {
    val indigoPlatformVersion = "<TOMTOM-INDIGO-VERSION>"

    // Optional: Plugin to configure in the default frontends and services from the TomTom IndiGO platform.
    implementation("com.tomtom.ivi.platform.gradle:api_defaults_core:$indigoPlatformVersion")

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

__Note:__ Some of these Gradle plugins add menu items. The order in which these plugins are applied,
defines the order of the menu items in the main menu frontend.

## Module references

Before adding frontends or IVI service hosts, our recommendation is to implement the
`ModuleReference` class.

The `ModuleReference` implementation class can be used in the build configurations of the frontend
and IVI service host later on. It will be used to refer to modules that implement the frontend or
IVI service host, and also to resolve the full-qualified package names.

Create the `ModuleReference` implementation class `<ProjectName>ModuleReference` in `buildSrc`.
You can use
[`ExampleModuleReference.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/dependencies/ExampleModuleReference.kt)
in
[`buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/dependencies/`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/tree/main/buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/dependencies)
as an example.
