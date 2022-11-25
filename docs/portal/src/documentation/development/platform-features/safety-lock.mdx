---
title: Safety Lock
layout: default
---

Safety lock is a feature that allows the UI to disable or change certain functionality depending on
whether or not it's safe for the driver to do so. For example, while driving it is commonly
considered unsafe for the driver to be typing, and so a frontend that allows text input may disable
its input field when safety lock is enabled.

Safety lock can be configured per
[_IVI instance_](/tomtom-digital-cockpit/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system),
allowing safety lock to be enabled on the driver display without impacting passenger displays. This
allows frontend plugins to remain agnostic as to whether the display is for a driver or passenger,
and simply use the service information as provided.

_Note:_ Configuring services per IVI instance is still under development and will be delivered in
an upcoming release.

## Using safety lock

The main interface for safety lock is the [`SafetyLockService`](TTIVI_PLATFORM_API), found in
[`platform_safetylock_api_service_safetylock`](TTIVI_PLATFORM_API). It exposes an observable `Boolean`
`isSafetyLockEnabled` to indicate whether safety lock is currently enabled.

The service has multiple instances to allow enabling safety lock on one display (for the driver)
without enabling it on others (such as for the passenger). The service implementation can either
determine this internally based on its [`IviInstanceId`](TTIVI_PLATFORM_API) or the service may be
configured to only be deployed in certain IVI instances. As the final product configures which
plugins are available in which IVI instance, the internal logic of a frontend or service plugin
cannot make assumptions on whether the [`SafetyLockService`](TTIVI_PLATFORM_API) is accessible to
them. [`SafetyLockService`](TTIVI_PLATFORM_API) should therefore be accessed through `createApiOrNull`
instead of `createApi`, allowing the plugin to gracefully handle the scenario where the
[`SafetyLockService`](TTIVI_PLATFORM_API) is not available with a `null`-check. This can be done as
shown in the following Kotlin example:

```kotlin
val isSafetyLockEnabled: LiveData<Boolean> =
    SafetyLockService.createApiOrNull(lifecycleOwner, iviServiceProvider)
        ?.isSafetyLockEnabled
        ?: ImmutableLiveData(false)
```

Note that `createApiOrNull` means that `?.isSafetyLockEnabled` may return `null`. The
`?: ImmutableLiveData(false)` is added here in order to assign a value of type `LiveData<Boolean>`
instead of a nullable `LiveData<Boolean>?` that has to be checked for `null` in usage.

### In frontends

The most common use-case for safety lock is in frontends which update the contents of fragment
layouts depending on whether safety lock is enabled. To facilitate this use-case,
[`FrontendViewModel`](TTIVI_PLATFORM_API) exposes a `LiveData<Boolean>` `isSafetyLockEnabled` that is
linked to the service. This allows XML layouts to directly use it in data binding without having to
manually set up a service connection.

For example, a frontend that has a ViewModel like:

```kotlin
class MyViewModel(panel: MyPanel) : FrontendViewModel<MyPanel>(panel) {
    fun onButtonClicked() {
        // Do something
    }
}
```

can be used to enable or disable a button depending on the safety lock state in an XML layout as
follows:

```xml
<Button
    android:text="@string/my_button_text"
    android:onClick="@{viewModel.onButtonClicked()}"
    android:enabled="@{viewModel.isSafetyLockEnabled}" />
```

In frontend logic that is outside of a ViewModel's scope, the safety lock state can be accessed
directly from the service.

### In services

Though not as common as usage in frontends, business logic that lives in services may also want to
adjust its output depending on whether safety lock is active. Similarly to frontend logic, services
can also access the safety lock state through `SafetyLockService.createApiOrNull`.

Note however that _only_ services with multiple instances (configured through
`@IviService(multipleInstances = true)`) can access [`SafetyLockService`](TTIVI_PLATFORM_API), as
[`SafetyLockService`](TTIVI_PLATFORM_API) is itself a service with multiple instances. Global services
that have a single instance for all displays cannot take safety lock state into account as they
facilitate various displays that can have different safety lock states. If you have a global service
that needs this information, it is an indicator that the service should perhaps not be global as it
requires instance-specific information and thus should itself be a service with multiple instances.

## Specifying safety lock criteria

The exact criteria for enabling safety lock depends on the service implementation. Common criteria
include the vehicle's speed or the gear stick position.

The stock implementation monitors the vehicle's speed through
[`VehicleDrivingStateService`](TTIVI_PLATFORM_API) and enables safety lock depending on the
`drivingState`. The stock implementation of that service in turn considers the state to be
[`DrivingState`](TTIVI_PLATFORM_API)`.DRIVING` when the speed reported by NavKit exceeds 5
kilometers an hour.

To change the stock behaviour, the service plugin `safetyLockServiceHost` can be replaced with a
custom one, implemented as seen fit. The pages
[Create an IVI Service](/tomtom-digital-cockpit/documentation/tutorials-and-examples/basics/create-an-ivi-service)
and
[Configure the Runtime Deployment of the IVI System](/tomtom-digital-cockpit/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system)
describe this in further detail.

## Safety lock static configuration

As explained in the introduction, the safety lock service is configured per _IVI instance_, see also
[Configure the Runtime Deployment of the IVI System](/tomtom-digital-cockpit/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system).

In addition, it is also possible to specify how the safety lock impacts the user experience.
For example, while driving it is commonly considered unsafe for the driver to be typing,
and therefore, a frontend that allows text input may disable its input when the safety lock is
enabled. It is possible to configure this restriction by changing the safety lock static
configuration, see also [Configuration Framework](/tomtom-digital-cockpit/documentation/development/platform-domains/configuration-framework).

The safety lock default configuration is defined as a resource file that contains configuration
keys and their values. The following keys and values are defined by default:

```xml
<resources>
    <!-- Indicates whether the text input fields are restricted by the safety lock mode when
    available or not; `true` by default. -->
    <bool name="isTextInputRestrictedBySafetyLockConfigKey">true</bool>
</resources>
```

If you don't want to restrict access to text input fields, you can configure it by setting the
`isTextInputRestrictedBySafetyLockConfigKey` to `false`:

```xml
<bool name="isTextInputRestrictedBySafetyLockConfigKey">false</bool>
```

__Note:__ It is the responsibility of service or frontend implementation, to take into account this
configuration, in addition to the [`SafetyLockService`](TTIVI_PLATFORM_API).
