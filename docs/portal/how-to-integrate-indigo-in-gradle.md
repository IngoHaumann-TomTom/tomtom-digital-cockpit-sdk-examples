# How to integrate Indigo in Gradle

This pages contains the steps required to integrate the IndiGO platform into an existing Android
Gradle project. When you are new to IndiGO, our recommendation is to use the examples as a starting
point as these examples already cover the steps described on this page. Use the steps in this page
as reference to integrate the IndiGO platform in an existing / newly created Android Gradle
project. This steps assume a basic level of experience with setting up Gradle build files.

## Maven repositories

Gradle will need to be able to download IndiGO platform dependencies from Maven repositories.

This requires the following Gradle configuration:

```kotlin
repositories {
    // Local artifact cache
    mavenLocal()

    maven("https://repo.tomtom.com/repository/ivi") {
        credentials {
            username = properties["nexusUsername"].toString()
            password = properties["nexusPassword"].toString()
        }
    }

    // External repositories
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")

    // PU LNS repo for the Connectivity Agent
    maven("https://maven.tomtom.com:8443/nexus/content/repositories/releases/")
}
```

The above needs to be applied in `buildscript`, `buildSrc` and in all projects. As such, place
the above in a file called `buildSrc/repositories.gradle.kts` and apply this file in
`buildSrc/build.gradle.kts` with:

```
buildscript {
    apply("repositories.gradle.kts")
}
apply("repositories.gradle.kts")
```

and in root project `build.gradle.kts` file:

```kotlin
apply(from = rootProject.file("buildSrc/repositories.gradle.kts"))
subprojects {
    apply(from = rootProject.file("buildSrc/repositories.gradle.kts"))
}
```

## BuildSrc dependencies

IndiGO platform provides Gradle plugins for the build time configuration of the Indigo platform.
To allow the Gradle plugins to be used in the Gradle projects it is required to add the gradle
plugins as `implementation` dependencies to the `buildSrc`. The following adds the dependencies:

**buildSrc/build.gradle.kts**

```kotlin

dependencies {
    val indigoPlatformVersion = ...

    // Optional: Plugin to configure the IVI application at build time.
    implementation("com.tomtom.ivi.platform.gradle:api_defaults_core:$indigoPlatformVersion")
    // Optional: Plugin to configure the NavKit2 API key at build time.
    implementation("com.tomtom.ivi.platform.gradle:api_defaults_navkit2:$indigoPlatformVersion")
    // Mandatory: Plugin to configure the IVI application at build time.
    implementation("com.tomtom.ivi.platform.gradle:api_framework_config:$indigoPlatformVersion")
    // Optional: Plugin for versioning the APK based on the Git repository information.
    implementation("com.tomtom.ivi.platform.gradle:api_tools_version:$indigoPlatformVersion")
}
```

## Root project configuration

Apply the following plugins in the top level `build.gradle.kts` file:

```kotlin
plugins {
    `kotlin-dsl`
    ...
    // Mandatory: For configure the IVI application at build time.
    id("com.tomtom.ivi.platform.framework.config")
    // Optional: Plugin for versioning the APK based on the Git repository information.
    id("com.tomtom.ivi.platform.tools.version")
}
```

In the same file, configure the IndiGO platform dependency source:

```kotlin
ivi {
    dependencySource = IviDependencySource.Artifactory(Versions.INDIGO_PLATFORM)
}
```

## Integrating IndiGO platform into the APK

To integrate IndiGO platform into an APK, you can add the following to the `build.gradle.kts`
of the project that builds the APK:

```kotlin

plugins {
    // Optional: To use defaults from the default IndiGO IVI application.
    id("com.tomtom.ivi.platform.defaults.core")

    // Optional: To configure the NavKit2 API key at build time.
    id("com.tomtom.ivi.platform.defaults.navkit2")
}

ivi {
    application {
        enabled = true
    }
}
```

## Module references

Before adding frontends or IVI service hosts, our recommendation is to implement the
`ModuleReference` class.

The `ModuleReference` implementation class can be used in the build configurations of the
frontend and IVI service host later on. It will be used to refer to modules that implement the
frontend or IVI service host and to resolve the full-qualified package names as well.

Create the `ModuleReference` implementation class (`<ProjectName>ModuleReference`) in `buildSrc`.
You can use `ExampleModuleReference.kt` in
`buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/dependencies/` as an example.

## Next steps

See:

- [How to create an IVI service](examples/how-to-create-ivi-service.html)
- [How to create a frontend plugin](examples/how-to-create-frontend-plugin.html)
