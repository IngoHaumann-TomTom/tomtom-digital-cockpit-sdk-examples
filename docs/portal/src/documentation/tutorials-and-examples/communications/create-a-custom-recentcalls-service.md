---
title: Create a Custom Recent Calls Service
---

The IVI platform comes with a [`RecentCallsService`](TTIVI_INDIGO_API) interface for accessing recent
calls information. A product may provide its own implementation of the
[`RecentCallsService`](TTIVI_INDIGO_API) interface or use an existing implementation. When
the existing implementation of a [`RecentCallsService`](TTIVI_INDIGO_API) is inadequate
(for example if we want to only show the recent calls of the day, filtering out older
calls) it is possible to define a custom recent calls service. In order for the UI to be able to
display recent calls from this service, a custom recent calls service needs to be written. This
document describes how to do this.

## How to create a custom recent calls service

The following sections describe how to create a custom recent calls service implementation.

The example code for the concepts presented here is provided in
`examples/telephone/customrecentcalls`.

### Service module setup

To create a custom recent calls service, add a manifest file to your module and add a dependency to
the [`RecentCallsService`](TTIVI_INDIGO_API) to your Gradle file.

Your
[`AndroidManifest.xml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/src/main/AndroidManifest.xml#L14)
should contain:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.telephony.customrecentcalls" />
```

Your
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/build.gradle.kts#L19)
file should contain:

```kotlin
dependencies {
    implementation(libraries.iviPlatformRecentcallsApiServiceRecentcalls)
}
```

### Service configuration

To configure a recent calls service to use your custom implementation, define a service host
configuration class that inherits from the `IviServiceHostConfig` class. This class should be placed
in the application Gradle build file.

[`examples/telephony/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/app/build.gradle.kts#L37-L48)

```kotlin
val customRecentCallsServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "CustomRecentCallsServiceHostBuilder",
        implementationModule = ExampleModuleReference("examples_telephony_customrecentcalls"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "RecentCallsService",
                serviceApiModule = IviPlatformModuleReference("platform_recentcalls_api_service_recentcalls")
            )
        )
    )
}
```

In this configuration, the `services_customrecentcalls` module defines the implementation for
the [`platform_recentcalls_api_service_recentcalls`](TTIVI_INDIGO_API) interface.

In order to create the service host configuration named `CustomRecentCallsServiceHost`, the IVI
platform needs a service host builder class with the specific
name `CustomRecentCallsServiceHostBuilder`.

[`src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsServiceHostBuilder.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsServiceHostBuilder.kt#L24-L33)

```kotlin
class CustomRecentCallsServiceHostBuilder : IviServiceHostBuilder() {

    override fun build(iviServiceHostContext: IviServiceHostContext) =
        SimpleIviServiceHost(
            iviServiceHostContext,
            setOf(CustomRecentCallsService(iviServiceHostContext))
        )

    companion object
}
```

__Note:__ Every service host configuration needs to be registered in your application. This is so
that the platform knows which service should be started with which implementation when a client
requires the access to a service api.

To register this configuration, add the service host to your application Gradle file:

[`examples/telephony/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/app/build.gradle.kts#L61-L64)

```kotlin
ivi {
    optInToExperimentalApis = true

    application {
        enabled = true
        services {
            // Replace the default recent calls service host with the custom recent calls service
            // host.
            removeHost(recentCallsServiceHost)
            addHost(customRecentCallsServiceHost)
        }
    }
}
```

### Service definition

In order to create a recent calls service implementation you need to create a class that
inherits from the `RecentCallsServiceBase` base class.

[`src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt#L27-L28)

```kotlin
internal class CustomRecentCallsService(iviServiceHostContext: IviServiceHostContext) :
    RecentCallsServiceBase(iviServiceHostContext) {
}
```

__Note:__ The recent calls service implementation can define any kind of source as the list of
recent calls, for example the Android
[CallLog provider](https://developer.android.com/reference/android/provider/CallLog).

In this example, the recent calls service always returns a list of two hard-coded calls. The list of
recent calls contains those two recent calls after initialization. The list `recentCallsSource` is
the source of recent calls:

[`src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt#L31-L46)

```kotlin
// The source of recent calls.
private val recentCallsSource = listOf(
    RecentCallsService.RecentCall(
        type = RecentCallsService.CallType.INCOMING,
        displayName = "Caller",
        phoneNumber = PhoneNumber("+2811111111", PhoneNumberType.Mobile),
        duration = Duration.ofSeconds(60),
        creationTime = Instant.now()
    ),
    RecentCallsService.RecentCall(
        type = RecentCallsService.CallType.OUTGOING,
        displayName = "Receiver",
        phoneNumber = PhoneNumber("+2822222222", PhoneNumberType.Work),
        duration = Duration.ZERO,
        creationTime = Instant.parse("2020-04-09T10:15:30.00Z")
    ),
)
```

### Service lifecycle

To manage the initialization and destruction of the service, override the `onCreate` and `onDestroy`
methods.

When the service is created:

[`src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt#L48-L63)

```kotlin
override fun onCreate() {
    super.onCreate()
    // Initialize the synchronization status.
    phoneBookSynchronizationStatus = PhoneBookSynchronizationStatus.NO_CONNECTED_DEVICES
    // Initialize the recentCallsDescending property with an empty list.
    recentCallsDescending = emptyList()
    // Set the service ready and clients can call any Api of the service.
    serviceReady = true
    // The source of recent calls is ready and synchronization starts.
    phoneBookSynchronizationStatus = PhoneBookSynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS
    // Update recentCallsDescending with the list of recent calls from the source.
    // Make sure that the list is in descending chronological order.
    // If a client (typically a view model) requires the list in a different order,
    // then it should resort the list before use.
    recentCallsDescending = recentCallsSource.sortedByDescending(RecentCall::creationTime)
}
```

then the [`RecentCallsService`](TTIVI_INDIGO_API) properties `recentCallsDescending` and
`phoneBookSynchronizationStatus` have been initialized.

When the service is destroyed:

[`src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customrecentcalls/src/main/kotlin/com/example/ivi/example/telephony/customrecentcalls/CustomRecentCallsService.kt#L65-L68)

```kotlin
override fun onDestroy() {
    // Put here cleaning code if necessary.
    super.onDestroy()
}
```

## External links

- TomTom IndiGO [`RecentCallsService`](TTIVI_INDIGO_API).
- Android [`CallLog provider`](https://developer.android.com/reference/android/provider/CallLog).

