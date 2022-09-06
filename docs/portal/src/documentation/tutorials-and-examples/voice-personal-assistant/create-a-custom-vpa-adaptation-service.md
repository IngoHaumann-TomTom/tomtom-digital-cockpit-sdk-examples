---
title: Create a Custom VPA Adaptation Service
---

The VPA Adaptation service provides an abstraction for a VPA engine, with which other TomTom IndiGO
components communicate.

The TomTom IndiGO platform includes a stock implementation of the VPA Adaptation Service for the
Alexa Auto VPA, and support for the Cerence Assistant VPA is in development. If TomTom IndiGO does
not support a specific VPA, or its stock implementation is not suitable for the customer's use case,
you can create your own VPA Adaptation Service.

## Example VPA app

The TomTom IndiGO SDK comes with an example app showing how to create a custom VPA Adaptation
Service, see directory `examples/vpa`.

## Create a custom VPA Adaptation Service

The following sections describe how to create a custom VPA Adaptation service implementation.

### Create a service implementation module

The new module depends on [`VpaAdaptationService`](TTIVI_INDIGO_API), declared in the package
[`com.tomtom.ivi.platform.vpa.api.service.vpaadaptation`](TTIVI_INDIGO_API).

[`/build-logic/libraries.versions.toml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build-logic/libraries.versions.toml#L67)

```toml
indigoPlatformVpaApiServiceVpaadaptation = { module = "com.tomtom.ivi.platform:platform_vpa_api_service_vpaadaptation", version.ref = "indigoPlatform" }
```

[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/build.gradle.kts#L21)

```kotlin
dependencies {
    implementation(libraries.indigoPlatformVpaApiServiceVpaadaptation)
}
```

The [`VpaAdaptationService`](TTIVI_INDIGO_API) API is experimental, and has to be explicitly opted in.

[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/build.gradle.kts#L17)

```kotlin
ivi {
    optInToExperimentalApis = true
}
```

### Implement the VPA Adaptation Service

The VPA Adaptation service can be implemented by defining a class that inherits from the abstract
`VpaAdaptationServiceBase` class and implements the methods defined in the
[`VpaAdaptationService`](TTIVI_INDIGO_API) interface.

The service implementation requires a number of properties to be set for configuring itself with the
TomTom IndiGO platform. The properties can be set by overriding the `onCreate()` method:

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L35-L65)

```kotlin
override fun onCreate() {
    super.onCreate()

    vpaProperties = VpaProperties(
        vpaIdentifier = VpaIdentifier(name = "Example VPA"),
        supportedLocales = List<Locale> = listOf(Locale.US),
        supportedCountries = List<String> = listOf("USA")
    )

    vpaAuthenticationStatus = VpaAuthenticationStatus(AuthenticationState.AUTHENTICATED)

    vpaAvailabilityState = VpaAvailabilityState.AVAILABLE

    dialogueState = DialogueState.IDLE

    alerts = emptyList()

    hasPendingNotifications = false

    vpaSettings = VpaSettings(
        wakeUpWordEnabled = false,
        activeLocales = emptyList(),
        enabledEarcons = EnumSet.noneOf(VpaEarconType::class.java),
        doNotDisturbModeEnabled = false,
        locationSharingEnabled = false,
        syncVehicleNavigationFavoritesEnabled = false,
        allowedPersonalData = emptyMap()
    )

    serviceReady = true
}
```

Depending on the features supported by your VPA, you will need to override other functions of this
service so that the VPA can react to certain actions by the user.

#### VPA Properties

Static information about the VPA, such as its name, supported locales and countries, are stored
in `vpaProperties`.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L38-L42)

```kotlin
vpaProperties = VpaProperties(
    vpaIdentifier = VpaIdentifier(name = "Example VPA"),
    supportedLocales = List<Locale> = listOf(Locale.US),
    supportedCountries = List<String> = listOf("USA")
)
```

#### Authentication Status

The current authentication state of the VPA is stored in `vpaAuthenticationStatus`. If the VPA
supports user authentication, the `logIn()` and `logOut()` functions should be overridden to allow a
client to authenticate with or log out of the VPA service. These functions should update the
`vpaAuthenticationStatus` to reflect the new situation.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L67-L79)

```kotlin
override fun onCreate() {
    vpaAuthenticationStatus = VpaAuthenticationStatus(AuthenticationState.NOT_AUTHENTICATED)
}

override suspend fun logIn() {
    // TODO: Log in to the VPA.

    // If the login is successful - update the status.
    vpaAuthenticationStatus = VpaAuthenticationStatus(AuthenticationState.AUTHENTICATED)
}

override suspend fun logOut() {
    // TODO: Log out of the VPA.

    // If the logout is successful - update the status.
    vpaAuthenticationStatus = VpaAuthenticationStatus(AuthenticationState.NOT_AUTHENTICATED)
}
```

Refer to [`VpaAuthenticationStatus`](TTIVI_INDIGO_API) for the details.

#### Availability State

Property `vpaAvailabilityState` indicates whether this VPA is ready to be used by clients.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L46)

```kotlin
vpaAvailabilityState = VpaAvailabilityState.AVAILABLE
```

#### Dialogue State

The state of the conversation with the VPA is reflected by `dialogueState`. This helps a UI indicate
whether, for example, the VPA is waiting for the user to reply to a question. It is essential for
the service to monitor the VPA dialogue state and update the property accordingly.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L48)

```kotlin
dialogueState = DialogueState.IDLE
```

#### Alerts

Property `alerts` is used to hold information about the alerts that have been scheduled through
interaction with the VPA.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L50)

```kotlin
alerts = emptyList()
```

#### Pending Notifications

Some of the VPA messages may not be played at once. Property `hasPendingNotifications` informs a
client whether the VPA has voice notifications ready to be played.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L52)

```kotlin
hasPendingNotifications = false
```

#### Settings

Property `vpaSettings` holds a number of settings of the VPA. The `VpaAdaptationService` provides
functions to update these settings; please refer to the [`VpaAdaptationService`](TTIVI_INDIGO_API) API
reference documentation for details.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L54-L62)

```kotlin
override fun onCreate() {
    vpaSettings = VpaSettings(
        wakeUpWordEnabled = false,
        activeLocales = emptyList(),
        enabledEarcons = EnumSet.noneOf(VpaEarconType::class.java),
        doNotDisturbModeEnabled = false,
        locationSharingEnabled = false,
        syncVehicleNavigationFavoritesEnabled = false,
        allowedPersonalData = emptyMap()
    )
}
```

#### Push-To-Talk

To define how the VPA reacts to a push-to-talk button press, override the `startListening()`
function. In this function you would typically inform the VPA engine that the user is ready to talk
to the VPA.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L112-L114)

```kotlin
override suspend fun startListening() {
    // TODO: Handle push-to-talk activation.
}
```

#### Privacy Mode

The privacy mode is controlled by the `enablePrivacyMode()` function.

[`src/main/kotlin/ExampleVpaService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaService.kt#L116-L118)

```kotlin
override suspend fun enablePrivacyMode(enable: Boolean) {
    // TODO: Enable or disable the privacy mode.
}
```

Please refer to the [`VpaAdaptationService`](TTIVI_INDIGO_API) API reference documentation for
detailed information on its properties and functions.

### Create a service host

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder.

- An `ExampleVpaServiceHost` class:<br/>
  [`src/main/kotlin/ExampleVpaServiceHost.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaServiceHost.kt#L21-L30)

   ```kotlin
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

   /**
    * An [ExampleVpaServiceHost] host server.
    */
   internal class ExampleVpaServiceHost(
       iviServiceHostContext: IviServiceHostContext,
       iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
   ) :
       IviServiceHostBase(iviServiceHostContext) {

       override val iviServices = setOf(
           ExampleVpaService(iviServiceHostContext, iviDiscoverableServiceIdProvider)
       )
   }
   ```

- An `ExampleVpaServiceHostBuilder` class:<br/>
  [`src/main/kotlin/ExampleVpaServiceHostBuilder.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/service/src/main/kotlin/ExampleVpaServiceHostBuilder.kt#L21-L29)

   ```kotlin
   /**
    * An [ExampleVpaServiceHost] builder used to build an [ExampleVpaService] host.
    */
   class ExampleVpaServiceHostBuilder : IviServiceHostBuilder() {

       override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
           ExampleVpaServiceHostBuilder(iviServiceHostContext) {
               getDiscoverableServiceId(it)
           }

       companion object
   }
   ```

Please ensure that the `ExampleVpaServiceHostBuilder` class is added to the root of your module
hierarchy.

### Configure the service host deployment

Define an IVI service host implementation in your Gradle file, This can also be defined in a
top-level gradle file (for example, `iviservicehosts.gradle.kts`) so it can be used in a
multi-project build, including the tests.

[`/examples/vpa/iviservicehosts.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/iviservicehosts.gradle.kts#L52-L66)

```kotlin
import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.dependencies.IviPlatformModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceInterfaceConfig

/**
 * Defines a configuration for the Example VPA service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */
val exampleVpaServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "ExampleVpaServiceHostBuilder",
        implementationModule = ExampleModuleReference("examples_vpa_service"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "VpaAdaptationService",
                serviceId = "com.example.ivi.example.vpa",
                serviceApiModule = IviPlatformModuleReference(
                    "platform_vpa_api_service_vpaadaptation"
                )
            )
        )
    )
}
```

Register the service host build configuration in the main application's build script.

[`examples/vpa/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/vpa/app/build.gradle.kts#L25-L35)

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

// Define the service host configs as defined in the top-level `iviservicehosts.gradle.kts` file.
apply(from = rootProject.file("examples/vpa/iviservicehosts.gradle.kts"))

// Use Gradle's extra extensions to obtain the `exampleVpaServiceHost` config as defined in
// the top-level `iviservicehosts.gradle.kts` file.
val exampleVpaServiceHost: IviServiceHostConfig by project.extra

ivi {
    application {
        enabled = true
        services {
            // Add the Example VPA service host to the application.
            addHost(exampleVpaServiceHost)
        }
    }
}

// The rest of the build script, dependencies, etc.
```
