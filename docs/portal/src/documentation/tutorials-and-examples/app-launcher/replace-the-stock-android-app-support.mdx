---
title: Replace the Stock Android App Support
---

The TomTom Digital Cockpit app launcher provides stock implementations for providing the list of Android apps
to show in the app launcher panel and for launching the Android apps when they are selected.

The stock Android app source provider will provide the list of Android apps which are present on the
device, and that were not installed as part of the TomTom Digital Cockpit system image or subsequent TomTom
Digital Cockpit updates. If there is a requirement to show or filter apps based on different criteria then
this can be done by overriding the stock app source provider.

An example of an alternative Android app source implementation could be one which returns a fixed
list of pre-installed apps.

TomTom Digital Cockpit provides stock implementations for launching "launchable" Android apps (that is,
standard Android apps with a launcher activity) and Android media apps (that is, media source apps
that can be launched in a media player). Launch handling for these two Android app types can be
overridden individually with separate implementations or both can be overridden together with a
single combined implementation.

An example of an alternative "launchable" Android app launch handler implementation could be one
which launches a separate standalone app rather than the stock behaviour of launching the app within
a panel.

This section shows how to:

- [Replace the stock Android app source support](#replace-the-stock-android-app-source-support)
- [Replace the stock launchable Android app launch support](#replace-the-stock-launchable-android-app-launch-support)

## Replace the stock Android app source support

The stock Android app source provider implementation can be overridden by creating your own
[`AppSourceProviderService`](TTIVI_PLATFORM_API) implementation and returning the
[`AndroidApp`](TTIVI_PLATFORM_API) class as the value of the
[`AppSourceProviderService`](TTIVI_PLATFORM_API)`.supportedAppClass`.

The following steps describe how to override the implementation of the stock
[`AndroidApp`](TTIVI_PLATFORM_API) source provider:

- [Create the replacement app source provider module](#create-the-replacement-app-source-provider-module)
- [Implement the replacement app source provider service](#implement-the-replacement-app-source-provider-service)
- [Create a service host for the replacement app source provider](#create-a-service-host-for-the-replacement-app-source-provider)
- [Configure the service host deployment for the replacement app source provider](#configure-the-service-host-deployment-for-the-replacement-app-source-provider)

### Create the replacement app source provider module

First add the app store common module and [`AppSourceProviderService`](TTIVI_PLATFORM_API)
dependency to the
[`/build-logic/libraries.versions.toml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/build-logic/libraries.versions.toml#L55-L56)
file:

```toml
iviAppsuiteAppstoreApiCommonModel = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_common_model", version.ref = "iviPlatform" }
iviAppsuiteAppstoreApiServiceAppsourceprovider = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_service_appsourceprovider", version.ref = "iviPlatform" }
```

Create a module for the [`AppSourceProviderService`](TTIVI_PLATFORM_API) implementation under
[`examples/applauncher/services`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/applauncher/services)
(for example
[`examples/applauncher/services/androidappsourceprovider`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/applauncher/services/androidappsourceprovider))
and add a Gradle build script.

Create a
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/androidappsourceprovider/build.gradle.kts#L12-L23)
file:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    // The AppSourceProviderService API is currently an experimental feature, and has to be
    // explicitly opted in.
    optInToExperimentalApis = true
}
dependencies {
    implementation(libraries.iviAppsuiteAppstoreApiCommonModel)
    implementation(libraries.iviAppsuiteAppstoreApiServiceAppsourceprovider)
}
```

The app source provider service module is an Android module, so it must also have an
`AndroidManifest.xml` file.

Create a
[`src/main/AndroidManifest.xml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/androidappsourceprovider/src/main/AndroidManifest.xml#L14)
file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.applauncher.services.androidappsourceprovider" />
```

### Implement the replacement app source provider service

The app source provider service can be implemented by defining a class (for example,
`AndroidAppSourceProviderService`) that inherits from the abstract `AppSourceProviderServiceBase`
base class and implements the methods defined in the
[`AppSourceProviderService`](TTIVI_PLATFORM_API) interface.

The service implementation needs to set a number of properties for configuring itself with the
TomTom Digital Cockpit platform. Please refer to the [`AppSourceProviderService`](TTIVI_PLATFORM_API) API
reference documentation for detailed information on these properties.

The properties can be set by overriding the `onCreate()` method.

Create
[`src/main/kotlin/com/example/ivi/example/applauncher/services/androidappsourceprovider/AndroidAppSourceProviderService.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/androidappsourceprovider/src/main/kotlin/com/example/ivi/example/applauncher/services/androidappsourceprovider/AndroidAppSourceProviderService.kt#L14-L45)

```kotlin
import com.tomtom.ivi.appsuite.appstore.api.common.model.ParcelableAppClass
import com.tomtom.ivi.appsuite.appstore.api.common.model.androidapptypes.AndroidApp
import com.tomtom.ivi.appsuite.appstore.api.common.model.androidapptypes.LaunchableAndroidApp
import com.tomtom.ivi.appsuite.appstore.api.service.appsourceprovider.AppSourceProviderServiceBase
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

class AndroidAppSourceProviderService(
    iviServiceHostContext: IviServiceHostContext,
    serviceIdProvider: IviDiscoverableServiceIdProvider
) : AppSourceProviderServiceBase(iviServiceHostContext, serviceIdProvider) {

    override fun onCreate() {
        super.onCreate()

        appStore = null

        // An IVI service interface can use only [Parcelable] types, so the [supportedAppClass] must
        // be returned inside a [Parcelable] wrapper class.
        supportedAppClass = ParcelableAppClass(AndroidApp::class.java)

        // The value of [launchablePackageName] should match the package name of an Android app
        // known to be installed on the device, otherwise the app will not appear in the app
        // launcher.
        installedApps = listOf(
            LaunchableAndroidApp(
                launchablePackageName = "com.android.example"
            )
        )
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }
}
```

### Create a service host for the replacement app source provider

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder. This can be achieved by creating two classes.

- An `AndroidAppSourceProviderServiceHost` class:

  [`src/main/kotlin/com/example/ivi/example/applauncher/services/androidappsourceprovider/AndroidAppSourceProviderServiceHost.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/androidappsourceprovider/src/main/kotlin/com/example/ivi/example/applauncher/services/androidappsourceprovider/AndroidAppSourceProviderServiceHost.kt#L14-L29)

  ```kotlin
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

  class AndroidAppSourceProviderServiceHost(
      iviServiceHostContext: IviServiceHostContext,
      iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
  ) : IviServiceHostBase(iviServiceHostContext) {

      override val iviServices = setOf(
          AndroidAppSourceProviderService(
              iviServiceHostContext,
              iviDiscoverableServiceIdProvider
          )
      )
  }
  ```

- An `AndroidAppSourceProviderServiceHostBuilder` class:

  [`src/main/kotlin/com/example/ivi/example/applauncher/services/androidappsourceprovider/AndroidAppSourceProviderServiceHostBuilder.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/androidappsourceprovider/src/main/kotlin/com/example/ivi/example/applauncher/services/androidappsourceprovider/AndroidAppSourceProviderServiceHostBuilder.kt#L14-L26)

  ```kotlin
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBuilder
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

  class AndroidAppSourceProviderServiceHostBuilder : IviServiceHostBuilder() {

     override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
         AndroidAppSourceProviderServiceHost(iviServiceHostContext) {
           getDiscoverableServiceId(it)
         }
     companion object
  }
  ```

### Configure the service host deployment for the replacement app source provider

__Note:__ Every service host needs to be configured and registered in your application. This is
necessary to know which service should be started with which implementation when a client requires
the access to a service api.

Define an IVI service host implementation in your gradle file. This can also be defined in a
top-level gradle file (for example, `iviservicehosts.gradle.kts`) so it can be used in a
multi-project build, including the tests.

Modify the
[`examples/applauncher/iviservicehosts.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/iviservicehosts.gradle.kts#L54-L67)
file:

```kotlin
/**
 * Defines a configuration for the android app source provider service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */

val androidAppSourceProviderServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "AndroidAppSourceProviderServiceHostBuilder",
        implementationModule = ExampleModuleReference("services_androidappsourceprovider"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AppSourceProviderService",
                serviceId = "com.tomtom.ivi.example.service.androidappsourceprovider",
                serviceApiModule = IviAppsuiteModuleReference("appsuite_appstore_api_service_appsourceprovider"),
                multipleInstances = true
            )
        )
    )
}
```

Register the service host build configuration in the main application's build script.

Modify the
[`examples/applauncher/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/app/build.gradle.kts#L37)
file:

```kotlin
apply(from = rootProject.file("examples/applauncher/iviservicehosts.gradle.kts"))

val androidAppSourceProviderServiceHost: IviServiceHostConfig by project.extra

ivi {
    application {
        enabled = true
        services {
            // Add the Android app source provider service host to the application.
            addHost(androidAppSourceProviderServiceHost)
        }
    }
}
```

## Replace the stock launchable Android app launch support

The stock Android app launch handler implementation can be overridden by creating your own
[`AppLaunchHandlerService`](TTIVI_PLATFORM_API) implementation and returning either the
[`LaunchableAndroidApp`](TTIVI_PLATFORM_API) or [`MediaAndroidApp`](TTIVI_PLATFORM_API) class as the
value of the [`AppLaunchHandlerService`](TTIVI_PLATFORM_API)`.supportedAppClass`.

In this example we will override the launching implementation for
[`LaunchableAndroidApp`](TTIVI_PLATFORM_API).

The following steps describe how to override the implementation of the stock
[`LaunchableAndroidApp`](TTIVI_PLATFORM_API) launch handler:

- [Create the replacement app launch handler module](#create-the-replacement-app-launch-handler-module)
- [Implement the replacement app launch handler service](#implement-the-replacement-app-launch-handler-service)
- [Create a service host for the replacement app launch handler](#create-a-service-host-for-the-replacement-app-launch-handler)
- [Configure the service host deployment for the replacement app launch handler](#configure-the-service-host-deployment-for-the-replacement-app-launch-handler)

### Create the replacement app launch handler module

First add the app store common module and [`AppLaunchHandlerService`](TTIVI_PLATFORM_API) dependency
to the
[`/build-logic/libraries.versions.toml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/build-logic/libraries.versions.toml#L57)
file:

```toml
iviAppsuiteAppstoreApiCommonModel = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_common_model", version.ref = "iviPlatform" }
iviAppsuiteAppstoreApiServiceApplaunchhandler = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_service_applaunchhandler", version.ref = "iviPlatform" }
```

Create a module for the [`AppLaunchHandlerService`](TTIVI_PLATFORM_API) implementation under
[`examples/applauncher/services`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/applauncher/services)
(for example
[`examples/applauncher/services/launchableandroidapplaunchhandler`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/applauncher/services/launchableandroidapplaunchhandler))
and add a Gradle build script.

Create a
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/launchableandroidapplaunchhandler/build.gradle.kts#L12-L23)
file:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    // The AppLaunchHandlerService API is currently an experimental feature, and has to be
    // explicitly opted in.
    optInToExperimentalApis = true
}
dependencies {
    implementation(libraries.iviAppsuiteAppstoreApiCommonModel)
    implementation(libraries.iviAppsuiteAppstoreApiServiceApplaunchhandler)
}
```

The app launch handler service module is an Android module, so it must also have an
`AndroidManifest.xml` file.

Create a
[`src/main/AndroidManifest.xml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/launchableandroidapplaunchhandler/src/main/AndroidManifest.xml#L14)
file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.applauncher.services.launchableandroidapplaunchhandler" />
```

### Implement the replacement app launch handler service

The app launch handler service can be implemented by defining a class (for example,
`LaunchableAndroidAppLaunchHandlerService`) that inherits from the abstract
`AppLaunchHandlerServiceBase` class and implements the methods defined in the
[`AppLaunchHandlerService`](TTIVI_PLATFORM_API) interface.

The service implementation needs to set a number of properties for configuring itself with the
TomTom Digital Cockpit platform. Please refer to the [`AppLaunchHandlerService`](TTIVI_PLATFORM_API) API
reference documentation for detailed information on these properties.

The properties can be set by overriding the `onCreate()` method.

Create
[`src/main/kotlin/com/example/ivi/example/applauncher/services/launchableandroidapplaunchhandler/LaunchableAndroidAppLaunchHandlerService.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/launchableandroidapplaunchhandler/src/main/kotlin/com/example/ivi/example/applauncher/services/launchableandroidapplaunchhandler/LaunchableAndroidAppLaunchHandlerService.kt#L14-L48)

```kotlin
import com.tomtom.ivi.appsuite.appstore.api.common.model.App
import com.tomtom.ivi.appsuite.appstore.api.common.model.ParcelableAppClass
import com.tomtom.ivi.appsuite.appstore.api.common.model.androidapptypes.LaunchableAndroidApp
import com.tomtom.ivi.appsuite.appstore.api.service.applaunchhandler.AppLaunchHandlerServiceBase
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

class LaunchableAndroidAppLaunchHandlerService(
    private val iviServiceHostContext: IviServiceHostContext,
    serviceIdProvider: IviDiscoverableServiceIdProvider
) : AppLaunchHandlerServiceBase(iviServiceHostContext, serviceIdProvider) {

    override fun onCreate() {
        super.onCreate()

        // An IVI service interface can use only [Parcelable] types, so the [supportedAppClass] must
        // be returned inside a [Parcelable] wrapper class.
        supportedAppClass = ParcelableAppClass(LaunchableAndroidApp::class.java)
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun launchApp(app: App) {
        require(app is LaunchableAndroidApp)
        launchLaunchableAndroidApp(app)
    }

    private fun launchLaunchableAndroidApp(app: LaunchableAndroidApp) {
        val launchIntent =
            iviServiceHostContext.context.packageManager.getLaunchIntentForPackage(app.packageName);
        iviServiceHostContext.context.startActivity(launchIntent)

    }
}
```

### Create a service host for the replacement app launch handler

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder. This can be achieved by creating two classes.

- A `LaunchableAndroidAppLaunchHandlerServiceHost` class:

  [`src/main/kotlin/com/example/ivi/example/applauncher/services/launchableandroidapplaunchhandler/LaunchableAndroidAppLaunchHandlerServiceHost.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/launchableandroidapplaunchhandler/src/main/kotlin/com/example/ivi/example/applauncher/services/launchableandroidapplaunchhandler/LaunchableAndroidAppLaunchHandlerServiceHost.kt#L14-L29)

  ```kotlin
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

  class LaunchableAndroidAppLaunchHandlerServiceHost(
      iviServiceHostContext: IviServiceHostContext,
      iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
  ) : IviServiceHostBase(iviServiceHostContext) {

      override val iviServices = setOf(
          LaunchableAndroidAppLaunchHandlerService(iviServiceHostContext, iviDiscoverableServiceIdProvider)
      )
  }
  ```

- A `LaunchableAndroidAppLaunchHandlerServiceHostBuilder` class:

  [`src/main/kotlin/com/example/ivi/example/applauncher/services/launchableandroidapplaunchhandler/LaunchableAndroidAppLaunchHandlerServiceHostBuilder.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/services/launchableandroidapplaunchhandler/src/main/kotlin/com/example/ivi/example/applauncher/services/launchableandroidapplaunchhandler/LaunchableAndroidAppLaunchHandlerServiceHostBuilder.kt#L14-L26)

  ```kotlin
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBuilder
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

  class LaunchableAndroidAppLaunchHandlerServiceHostBuilder : IviServiceHostBuilder() {

    override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
        LaunchableAndroidAppLaunchHandlerServiceHost(iviServiceHostContext) {
          getDiscoverableServiceId(it)
        }

    companion object
  }
  ```

### Configure the service host deployment for the replacement app launch handler

__Note:__ Every service host needs to be configured and registered in your application. This is
necessary to know which service should be started with which implementation when a client requires
the access to a service api.

Define an IVI service host implementation, in your gradle file, This can also be defined in a
top-level gradle file (for example, `iviservicehosts.gradle.kts`) so it can be used in a
multi-project build, including the tests.

Modify the
[`examples/applauncher/iviservicehosts.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/iviservicehosts.gradle.kts#L69-L82)
file:

```kotlin
/**
 * Defines a configuration for the launchable android app launch handler service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */
val launchableAndroidAppLaunchHandlerServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "LaunchableAndroidAppLaunchHandlerServiceHostBuilder",
        implementationModule = ExampleModuleReference("services_launchableandroidapplaunchhandler"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AppLaunchHandlerService",
                serviceId = "com.tomtom.ivi.example.service.launchableandroidapplaunchhandler",
                serviceApiModule = IviAppsuiteModuleReference("appsuite_appstore_api_service_applaunchhandler"),
                multipleInstances = true
            )
        )
    )
}
```

Register the service host build configuration in the main application's build script.

Modify the
[`examples/applauncher/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/applauncher/app/build.gradle.kts#L38)
file:

```kotlin
apply(from = rootProject.file("examples/applauncher/iviservicehosts.gradle.kts"))

val launchableAndroidAppLaunchHandlerServiceHost: IviServiceHostConfig by project.extra

ivi {
    application {
        enabled = true
        services {
            // Add the Android app launch handler service host to the application.
            addHost(launchableAndroidAppLaunchHandlerServiceHost)
        }
    }
}
```
