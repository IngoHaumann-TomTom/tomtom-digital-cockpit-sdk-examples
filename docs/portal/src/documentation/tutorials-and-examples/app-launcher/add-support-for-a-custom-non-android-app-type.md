---
title: Add Support for a Custom Non-Android App Type
---

For a custom, non-Android, app type, a means to access the list of available apps to display in the
App Launcher and also a means to launch these apps will be required. To do this the
[`AppSourceProviderService`](TTIVI_INDIGO_API) and [`AppLaunchHandlerService`](TTIVI_INDIGO_API)
will need to be implemented.

The following steps describe how to create source provider and launch handler service
implementations, using the example of a _web app_:

1. [Create the web app type](#create-the-web-app-type)
2. [Create the web app source provider](#create-the-web-app-source-provider)
3. [Create the web app launch handler](#create-the-web-app-launch-handler)

## Create the web app type

The new app type will need to be visible from a number of other modules, so create a new module for
it under `examples/applauncher/common`. Name the module `webapp`, and add a Gradle build script.

Create `build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    // The App base class is currently flagged as experimental, and has to be explicitly opted in.
    optInToExperimentalApis = true
}

dependencies {
    implementation(libraries.indigoAppsuiteAppstoreApiCommonModel)
}
```

Declare an app store dependency in the `/build-logic/libraries.versions.toml` file:

```toml
indigoAppsuiteAppstoreApiCommonModel = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_common_model", version.ref = "indigoPlatform" }
```

The app launcher common module is an Android module, so it must also have an `AndroidManifest.xml`
file.

Create `src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.applauncher.common.webapp" />
```

Declare the custom app type.

Create `src/main/kotlin/com/example/ivi/example/applauncher/common/webapp/WebApp.kt`:

```kotlin
import com.tomtom.ivi.appsuite.appstore.api.common.model.App
import com.tomtom.tools.android.api.resourceresolution.drawable.DrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.StringResolver
import kotlinx.parcelize.Parcelize

@Parcelize
class WebApp(
    val url: String,
    override val displayName: StringResolver,
    override val summary: StringResolver,
    override val icon: DrawableResolver,
) : App {

    override val id: String = "${this::class.java.simpleName}.$url"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebApp

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int = url.hashCode()
}
```

## Create the web app source provider

The following steps describe how to create a custom app source provider service implementation:

1. [Create the web app source provider service module](#create-the-web-app-source-provider-service-module)
2. [Implement the web app source provider service](#implement-the-web-app-source-provider-service)
3. [Create the web app source provider service host](#create-the-web-app-source-provider-service-host)
4. [Configure the web app source provider service host deployment](#configure-the-web-app-source-provider-service-host-deployment)

### Create the web app source provider service module

First, add the [`AppSourceProviderService`](TTIVI_INDIGO_API) dependency to the
`/build-logic/libraries.versions.toml` file:

```toml
indigoAppsuiteAppstoreApiServiceAppsourceprovider = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_service_appsourceprovider", version.ref = "indigoPlatform" }
```

Create a module for the [`AppSourceProviderService`](TTIVI_INDIGO_API) implementation under
`examples/applauncher/services` (for example `examples/applauncher/services/webappsourceprovider`)
and add a Gradle build script.

Create `build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    // The AppSourceProviderService API is currently an experimental feature, and has to be
    // explicitly opted in.
    optInToExperimentalApis = true
}
dependencies {
    api(project(":examples_applauncher_common_webapp"))
    implementation(libraries.indigoAppsuiteAppstoreApiServiceAppsourceprovider)
}
```

The app source provider service module is an Android module, so it must also have an
`AndroidManifest.xml` file.

Create `src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.applauncher.services.webappsourceprovider" />
```

### Implement the web app source provider service

The app source provider service can be implemented by defining a class (for example,
`WebAppSourceProviderService`) that inherits from the abstract `AppSourceProviderServiceBase` base
class, and implements the methods defined in the [`AppSourceProviderService`](TTIVI_INDIGO_API)
interface.

The service implementation needs to set a number of properties for configuring itself with the
TomTom IndiGO platform. Please refer to the [`AppSourceProviderService`](TTIVI_INDIGO_API) API
reference documentation for detailed information on these properties.

The properties can be set by overriding the `onCreate()` method.

Create `src/main/kotlin/com/example/ivi/example/applauncher/services/webappsourceprovider/WebAppSourceProviderService.kt`:

```kotlin
import com.tomtom.ivi.appsuite.appstore.api.common.model.ParcelableAppClass
import com.tomtom.ivi.appsuite.appstore.api.service.appsourceprovider.AppSourceProviderServiceBase
import com.tomtom.ivi.example.common.webapp.WebApp
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext
import com.tomtom.tools.android.api.resourceresolution.drawable.ResourceDrawableResolver
import com.tomtom.tools.android.api.resourceresolution.string.StaticStringResolver

class WebAppSourceProviderService(
    iviServiceHostContext: IviServiceHostContext,
    serviceIdProvider: IviDiscoverableServiceIdProvider
) : AppSourceProviderServiceBase(iviServiceHostContext, serviceIdProvider) {

    override fun onCreate() {
        super.onCreate()

        appStore = null

        // An IVI service interface can use only [Parcelable] types, so the [supportedAppClass] 
        // must be returned inside a [Parcelable] wrapper class.
        supportedAppClass = ParcelableAppClass(WebApp::class.java)

        installedApps = listOf(
            WebApp(
                url = "https://www.tomtom.com/",
                displayName = StaticStringResolver("TomTom"),
                summary = StaticStringResolver("TomTom Web"),
                icon = ResourceDrawableResolver(R.drawable.ttivi_appstore_icon_invalidpackage)
            )
        )
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }
}
```

### Create the web app source provider service host

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder. This can be achieved by creating two classes.

1. A `WebAppSourceProviderServiceHost` class:

   `src/main/kotlin/com/example/ivi/example/applauncher/services/webappsourceprovider/WebAppSourceProviderServiceHost.kt`:

   ```kotlin
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

   class WebAppSourceProviderServiceHost(
       iviServiceHostContext: IviServiceHostContext,
       iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
   ) : IviServiceHostBase(iviServiceHostContext) {

       override val iviServices = setOf(
           WebAppSourceProviderService(iviServiceHostContext, iviDiscoverableServiceIdProvider)
       )
   }
   ```

2. A `WebAppSourceProviderServiceHostBuilder` class:

   `src/main/kotlin/com/example/ivi/example/applauncher/services/webappsourceprovider/WebAppSourceProviderServiceHostBuilder.kt`:

   ```kotlin
   /**
    * A [WebAppSourceProviderServiceHost] builder used to build a [WebAppSourceProviderService]
    * host.
    */
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBuilder
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

   class WebAppSourceProviderServiceHostBuilder : IviServiceHostBuilder() {

      override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
          WebAppSourceProviderServiceHost(iviServiceHostContext) {
            getDiscoverableServiceId(it)
          }

      companion object
   }
   ```

### Configure the web app source provider service host deployment

__Note__
Every service host needs to be configured and registered in your application. This is necessary to
know which service should be started with which implementation when a client requires the access to
a service api.

Configure and add the service host in your application Gradle file:

```kotlin
apply(from = rootProject.file("iviservicehosts.gradle.kts"))

/**
 * Defines a configuration for the web app source provider service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */

val webAppSourceProviderServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "WebAppSourceProviderServiceHostBuilder",
        implementationModule = ExampleModuleReference("services_webappsourceprovider"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AppSourceProviderService",
                serviceId = "com.tomtom.ivi.example.service.webappsourceprovider",
                serviceApiModule = IviAppsuiteModuleReference("appsuite_appstore_api_service_appsourceprovider"),
                multipleInstances = true
            )
        )
    )
}

ivi {
    application {
        enabled = true
        services {
            // Add the web app source provider service host to the application.
            addHost(webAppSourceProviderServiceHost)
        }
    }
}
```

## Create the web app launch handler

The following steps describe how to create a web app launch handler service implementation:

1. [Create the web app launch handler service module](#create-the-web-app-launch-handler-service-module)
2. [Implement the web app launch handler service](#implement-the-web-app-launch-handler-service)
3. [Create the web app launch handler service host](#create-the-web-app-launch-handler-service-host)
4. [Configure the web app launch handler service host deployment](#configure-the-web-app-launch-handler-service-host-deployment)

### Create the web app launch handler service module

First, add the [`AppLaunchHandlerService`](TTIVI_INDIGO_API) dependency to the
`/build-logic/libraries.versions.toml` file:

```toml
indigoAppsuiteAppstoreApiServiceApplaunchhandler = { module = "com.tomtom.ivi.appsuite:appsuite_appstore_api_service_applaunchhandler", version.ref = "indigoPlatform" }
```

Create a module for the [`AppLaunchHandlerService`](TTIVI_INDIGO_API) implementation under
`examples/applauncher/services` (for example `examples/applauncher/services/webapplaunchhandler`)
and add a Gradle build script.

Create `build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    // The AppLaunchHandlerService API is currently an experimental feature, and has to be
    // explicitly opted in.
    optInToExperimentalApis = true
}
dependencies {
    api(project(":examples_applauncher_common_webapp"))
    implementation(libraries.indigoAppsuiteAppstoreApiServiceApplaunchhandler)
}
```

The app launch handler service module is an Android module, so it must also have
an `AndroidManifest.xml`
file.

Create `src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.applauncher.services.webapplaunchhandler" />
```

### Implement the web app launch handler service

The app launch handler service can be implemented by defining a class (for example,
`WebAppLaunchHandlerService`) that inherits from the abstract `AppLaunchHandlerServiceBase` base
class, and implements the methods defined in the [`AppLaunchHandlerService`](TTIVI_INDIGO_API)
interface.

The service implementation needs to set a number of properties for configuring itself with the
TomTom IndiGO platform. Please refer to the [`AppLaunchHandlerService`](TTIVI_INDIGO_API) API
reference documentation for detailed information on these properties.

The properties can be set by overriding the `onCreate()` method.

Create `src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchhandler/WebAppLaunchHandlerService.kt`:

```kotlin
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.tomtom.ivi.appsuite.appstore.api.common.model.App
import com.tomtom.ivi.appsuite.appstore.api.common.model.ParcelableAppClass
import com.tomtom.ivi.appsuite.appstore.api.service.applaunchhandler.AppLaunchHandlerServiceBase
import com.tomtom.ivi.example.common.webapp.WebApp
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

class WebAppLaunchHandlerService(
    private val iviServiceHostContext: IviServiceHostContext,
    serviceIdProvider: IviDiscoverableServiceIdProvider
) : AppLaunchHandlerServiceBase(iviServiceHostContext, serviceIdProvider) {

    override fun onCreate() {
        super.onCreate()


        // An IVI service interface can use only [Parcelable] types, so the [supportedAppClass]
        // must be returned inside a [Parcelable] wrapper class.
        supportedAppClass = ParcelableAppClass(WebApp::class.java)
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun launchApp(app: App) {
        require(app is WebApp)
        launchWebAppInBrowser(app)
    }

    private fun launchWebAppInBrowser(app: WebApp) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(app.url))
        iviServiceHostContext.context.startActivity(webIntent)
    }
}
```

### Create the web app launch handler service host

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder. This can be achieved by creating two classes.

1. A `WebAppLaunchHandlerServiceHost` class:

   `src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchhandler/WebAppLaunchHandlerServiceHost.kt`:

   ```kotlin 
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

   class WebAppLaunchHandlerServiceHost(
       iviServiceHostContext: IviServiceHostContext,
       iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
   ) : IviServiceHostBase(iviServiceHostContext) {

       override val iviServices = setOf(
           WebAppLaunchHandlerService(iviServiceHostContext, iviDiscoverableServiceIdProvider)
       )
   }
   ```

2. A `WebAppLaunchHandlerServiceHostBuilder` class:

   `src/main/kotlin/com/example/ivi/example/applauncher/services/webapplaunchhandler/WebAppLaunchHandlerServiceHostBuilder.kt`:

   ```kotlin   
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBuilder
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

   class WebAppLaunchHandlerServiceHostBuilder : IviServiceHostBuilder() {

     override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
         WebAppLaunchHandlerServiceHost(iviServiceHostContext) {
           getDiscoverableServiceId(it)
         }

     companion object
   }
   ```

### Configure the web app launch handler service host deployment

__Note__
Every service host needs to be configured and registered in your application. This is necessary to
know which service should be started with which implementation when a client requires the access to
a service api.

Configure and add the service host in your application Gradle file:

```kotlin
apply(from = rootProject.file("iviservicehosts.gradle.kts"))

/**
 * Defines a configuration for the web app launch handler service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */

val webAppLaunchHandlerServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "WebAppLaunchHandlerServiceHostBuilder",
        implementationModule = ExampleModuleReference("services_webapplaunchhandler"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AppLaunchHandlerService",
                serviceId = "com.tomtom.ivi.example.service.webapplaunchhandler",
                serviceApiModule = IviAppsuiteModuleReference("appsuite_appstore_api_service_applaunchhandler"),
                multipleInstances = true
            )
        )
    )
}

ivi {
    application {
        enabled = true
        services {
            // Add the web app launch handler service host to the application.
            addHost(webAppLaunchHandlerServiceHost)
        }
    }
}
```
