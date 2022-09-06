---
title: Add Support for Web App Launch Trigger
---

TomTom IndiGO provides a means for app launching functionality to be triggered from an external
source, like an App Store, by providing an implementation for
[`AppLaunchTriggerService`](TTIVI_INDIGO_API). For example, when a third-party app store broadcasts
an intent to launch an app, the implementation of this interface could observe such a broadcast and
trigger the launch functionality associated with that app type.

In this example, we will continue where we left off after adding an implementation for
[`AppLaunchHandlerService`](TTIVI_INDIGO_API) to support launching _web apps_ (see
[`Add support for a custom non-Android app type`](/tomtom-indigo/documentation/tutorials-and-examples/app-launcher/add-support-for-a-custom-non-android-app-type)),
as adding app launch trigger for any [`App`](TTIVI_INDIGO_API) type requires having an
implementation for [`AppLaunchHandlerService`](TTIVI_INDIGO_API) for the same app type.

In this example, we will be using the Android Debug Bridge
([ADB](https://developer.android.com/studio/command-line/adb)) to fire a broadcast with the required
information from the command-line. In production it would be expected that the broadcast would come
from an external app.

The following steps describe how to add support for triggering _web app_ launching by receiving a
broadcast:

- [Create web app launch trigger service module](#create-web-app-launch-trigger-service-module).
- [Create web app launch trigger broadcast receiver](#create-web-app-launch-trigger-broadcast-receiver).
- [Implement web app launch trigger service](#implement-web-app-launch-trigger-service).
- [Create web app launch trigger service host](#create-web-app-launch-trigger-service-host).
- [Configure web app launch trigger service host deployment](#configure-web-app-launch-trigger-service-host-deployment).
- [Send web app launch trigger broadcast](#send-web-app-launch-trigger-broadcast).

## Create web app launch trigger service module

First, add the [`AppLaunchTriggerService`](TTIVI_INDIGO_API) dependency to the
[`/build-logic/libraries.versions.toml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build-logic/libraries.versions.toml#L58)
file:

```toml
indigoAppsuiteAppstoreApiServiceApplaunchtrigger = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_service_applaunchtrigger", version.ref = "indigoPlatform" }
```

Create a new module (for example,
[`webapplaunchtrigger`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/tree/main/examples/applauncher/services/webapplaunchtrigger))
under
[`examples/applauncher/services`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/tree/main/examples/applauncher/services),
and add a Gradle build script.

Create a
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/services/webapplaunchtrigger/build.gradle.kts#L12-L21)
file:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    optInToExperimentalApis = true
}

dependencies {
    api(project(":examples_applauncher_common_webapp"))
    implementation(libraries.indigoAppsuiteAppstoreApiServiceApplaunchtrigger)
}
```

The app launch trigger module is an Android module, so it must also have an `AndroidManifest.xml`
file.

Create a
[`src/main/AndroidManifest.xml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/services/webapplaunchtrigger/src/main/AndroidManifest.xml#L14)
file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.applauncher.services.webapplaunchtrigger" />
```

## Create web app launch trigger broadcast receiver

To trigger the launch of the _web app_, we need to be able to receive and extract the information
sent through the [broadcast](https://developer.android.com/guide/components/broadcasts). Therefore,
we need to create a subclass of
[BroadcastReceiver](https://developer.android.com/reference/android/content/BroadcastReceiver)
(for example, `WebAppLaunchTriggerBroadcastReceiver`) and implement `onReceive(Context, Intent)`, so
we can extract the required information for launching a _web app_.

Let's assume that the _web app_ URL value will be sent with a key name `url`.

Create
[`src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerBroadcastReceiver.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/services/webapplaunchtrigger/src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerBroadcastReceiver.kt#L14-L40)

```kotlin
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ivi.example.applauncher.common.webapp.WebApp
import com.tomtom.tools.android.api.resourceresolution.drawable.ResourceDrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.StaticStringResolver

class WebAppLaunchTriggerBroadcastReceiver : BroadcastReceiver() {

    private val mutableTriggeredWebApp = MutableLiveData<WebApp>()
    val triggeredWebApp: LiveData<WebApp> = mutableTriggeredWebApp

    override fun onReceive(context: Context?, intent: Intent?) {
        // [WebApp.displayName], [WebApp.summary] and [WebApp.icon] are not necessary for launching
        // a [WebApp], therefore dummy data is assigned for these values.
        intent?.apply {
            mutableTriggeredWebApp.value = WebApp(
                url = getStringExtra("url") ?: "",
                displayName = StaticStringResolver("Dummy display name"),
                summary = StaticStringResolver("Dummy summary"),
                icon = ResourceDrawableResolver(R.drawable.ttivi_appstore_icon_invalidpackage)
            )
        }
    }
}
```

## Implement web app launch trigger service

The app launch trigger service can be implemented by defining a class (for example,
`WebAppLaunchTriggerService`) that inherits from the abstract `AppLaunchTriggerServiceBase` base
class, and provides the required implementation for the
[`AppLaunchTriggerService`](TTIVI_INDIGO_API) interface.

For an instance of `WebAppLaunchTriggerBroadcastReceiver` to receive a broadcast, we need to
register it with an
[`IntentFilter`](https://developer.android.com/reference/android/content/IntentFilter),
which identifies the broadcast action.

Create
[`src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/services/webapplaunchtrigger/src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerService.kt#L14-L47)

```kotlin
import android.content.IntentFilter
import com.tomtom.ivi.appsuite.appstore.api.service.applaunchtrigger.AppLaunchTriggerServiceBase
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

class WebAppLaunchTriggerService(
    iviServiceHostContext: IviServiceHostContext,
    serviceIdProvider: IviDiscoverableServiceIdProvider
) : AppLaunchTriggerServiceBase(iviServiceHostContext, serviceIdProvider) {

    override fun onCreate() {
        super.onCreate()

        val webAppLaunchTriggerBroadcastReceiver = WebAppLaunchTriggerBroadcastReceiver()
        val filter = IntentFilter(WEB_APP_LAUNCH_TRIGGER_ACTION_BROADCAST)
        context.registerReceiver(webAppLaunchTriggerBroadcastReceiver, filter)

        webAppLaunchTriggerBroadcastReceiver.triggeredWebApp.observe(this) {
            appLaunchTriggerEventListener.onAppLaunchTriggered(it)
        }

        serviceReady = true
    }

    private companion object {
        const val WEB_APP_LAUNCH_TRIGGER_ACTION_BROADCAST =
            "com.example.ivi.webapplaunchtrigger.action"
    }
}
```

## Create web app launch trigger service host

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder. This can be achieved by creating two classes.

- A `WebAppLaunchTriggerServiceHost` class:

  [`src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerServiceHost.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/services/webapplaunchtrigger/src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerServiceHost.kt#L14-L26)

  ```kotlin
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

  class WebAppLaunchTriggerServiceHost(
  iviServiceHostContext: IviServiceHostContext,
  iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
  ) : IviServiceHostBase(iviServiceHostContext) {

      override val iviServices = setOf(
          WebAppLaunchTriggerService(iviServiceHostContext, iviDiscoverableServiceIdProvider)
      )
  }
  ```

- A `WebAppLaunchTriggerServiceHostBuilder` class:

  [`src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerServiceHostBuilder.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/services/webapplaunchtrigger/src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchtrigger/WebAppLaunchTriggerServiceHostBuilder.kt#L14-L26)

  ```kotlin
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBuilder
  import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

  class WebAppLaunchTriggerServiceHostBuilder : IviServiceHostBuilder() {

      override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
          WebAppLaunchTriggerServiceHost(iviServiceHostContext) {
              getDiscoverableServiceId(it)
          }

      companion object
  }
  ```

## Configure web app launch trigger service host deployment

__Note:__ Every service host needs to be configured and registered in your application. This is
necessary to know which service should be started with which implementation, when a client requires
the access to a service API.

Define an IVI service host implementation in your gradle file. This can also be defined in a 
top-level gradle file (for example, `iviservicehosts.gradle.kts`) so it can be
used in a multi-project build, including the tests.

Modify the
[`examples/applauncher/iviservicehosts.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/iviservicehosts.gradle.kts#L84-L97)
file:

```kotlin
/**
 * Defines a configuration for the web app source provider service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */
val webAppLaunchTriggerServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "WebAppLaunchTriggerServiceHostBuilder",
        implementationModule = ExampleModuleReference("examples_applauncher_services_webapplaunchtrigger"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AppLaunchTriggerService",
                serviceId = "com.example.ivi.example.applauncher.services.webapplaunchtrigger",
                serviceApiModule = IviAppsuiteModuleReference("appsuite_appstore_api_service_applaunchtrigger"),
                multipleInstances = true
            )
        )
    )
}
```

Register the service host build configuration in the main application's build script.

Modify the
[`examples/applauncher/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/applauncher/app/build.gradle.kts#L39)
file:

```kotlin
apply(from = rootProject.file("examples/applauncher/iviservicehosts.gradle.kts"))

val webAppLaunchTriggerServiceHost: IviServiceHostConfig by project.extra

ivi {
    application {
        enabled = true
        services {
            // Add the web app launch trigger service host to the application.
            addHost(webAppLaunchTriggerServiceHost)
        }
    }
}
```

## Send web app launch trigger broadcast

After setting up everything we need to test the implementation by sending a broadcast from our
development machine. To do this we can use the `adb` command-line tool which provides a command for
sending a 
[broadcast](https://developer.android.com/studio/command-line/adb#:~:text=broadcast%20%5Boptions%5D%20intent).

__Note:__ Make sure that the action sent through the broadcast is identical to the action defined in
the [IntentFilter](https://developer.android.com/reference/android/content/IntentFilter) that was
previously registered for the broadcast.

```cmd
adb shell "am broadcast -a com.example.ivi.webapplaunchtrigger.action --es url 'https://www.tomtom.com/'"
```
