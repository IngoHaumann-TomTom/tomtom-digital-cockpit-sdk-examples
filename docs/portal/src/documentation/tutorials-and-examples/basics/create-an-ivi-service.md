---
title: Create an IVI Service
---

## Introduction of the IVI application architecture

An IVI application consists of IVI services for the business logic and frontends for the UI, which
combines layouts, panels, and view models. Panels and associated view models are rather short-living
components that are created to display something on screen, and are destroyed as soon as content
disappears. Services live much longer, almost matching the application lifetime. Apart from
Application Platform services, there can be application services that work with dedicated
frontends. Such services may suit as a model for a frontend.

## Overview of the example application

The example application replaces TomTom IndiGO's user profile frontend with the account frontend.
The account frontend adds new panels to show account information or a login page, which can be
invoked by a menu item 
(see [frontend plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin))
Account status is managed by the accounts service. The source code for the frontend and service can
be found in the following folders in the example app source:

- `examples/plugin/serviceapi/`
- `examples/plugin/service/`

## The plan

The accounts service will provide methods to login and logout a user, and keep the current logged
user.

To create an IVI service, you need to perform the following steps:

1. [Define an IVI service interface](#the-ivi-service-interface).
2. [Create an implementation of the IVI service interface](#the-ivi-service-implementation).
3. [Deploy the service](#service-deployment).
4. [Use the service API in the client side code](#use-the-ivi-service-in-the-client-side-code).

## IVI service interface project

### Project configuration

An IVI service interface should be defined in a dedicated module, so it can be used both by the
service implementation, and the service's clients.

To define the service interface, first create a new module at `examples/plugin/serviceapi` and add
a build script.

Create `build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

// Gradle extension to configure a project.
ivi {
    // Set the flag to configure that the project contains the definition of an IVI service.
    serviceApi = true
}

// The rest of the project configuration...
```

The project configuration above creates an IVI service project. To create an IVI settings service
project, set `settingsServiceApi` to true instead of `serviceApi`.

The IVI service interface project is an Android project, so it must have an `AndroidManifest.xml`
file.

Create `src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest package="com.example.ivi.example.plugin.serviceapi" />
```

### The IVI service interface

When the project is configured, create a Kotlin `interface` class, annotated with the 
[`@IviService`](TTIVI_INDIGO_API) annotation, which takes one mandatory argument `serviceId` which 
specifies the unique identifier that is used by client connections.

Create `src/main/kotlin/com/example/ivi/example/plugin/serviceapi/AccountsService.kt`:

```kotlin
// The annotation for an IVI service interface.
import com.tomtom.ivi.platform.framework.api.ipc.iviserviceannotations.IviService
// The annotation for IVI service functions.
import com.tomtom.ivi.platform.framework.api.ipc.iviserviceannotations.IviServiceFun

@IviService(
    serviceId = "com.example.ivi.example.plugin.service"
)
interface AccountsService {
    val activeAccount: Account?

    @IviServiceFun
    suspend fun logIn(username: String, password: SensitiveString): Boolean

    @IviServiceFun
    suspend fun logOut()

    // The service interface must at least contain the empty companion object, which will be
    // extended by the IVI service framework.
    // Kotlin does not allow extending the companion object if it has not been declared.
    companion object
}
```

The interface may have nullable or non-nullable values, methods, and event listener interfaces. All
methods must have the `suspend` modifier and the [`@IviServiceFun`](TTIVI_INDIGO_API) annotation. 
The [`@IviServiceFun`](TTIVI_INDIGO_API) annotation is required to distinguish methods of the 
service interface from auxiliary methods added by Kotlin/Java compiler. The interface must at 
least contain the empty companion object, which will be extended by the IVI service framework with 
a few methods, such as `createApi` and `createApiOrNull`. Kotlin does not allow extending the companion object if it has not been declared.

The package may have multiple service interfaces defined, although each must have a distinct
identifier. The package may contain other classes, functions, and any other Kotlin code like a
regular Kotlin package.

#### Parcelable types

An IVI service uses the Android Binder framework to transfer data between clients and the service.
Therefore, an IVI service interface can use only parcelable types, that implement
the [`Parcelable` interface](https://developer.android.com/reference/android/os/Parcelable).
The `kotlin-parcelize` plugin provides
a [Parcelable implementation generator](https://developer.android.com/kotlin/parcelize). When you
annotate a class with `@Parcelize`, a `Parcelable` implementation is automatically generated.

#### Map types

While the service interface may use properties of `Map<K,V>` it is not recommended because it is
inefficient. Any change of such property will require the transfer of a full map object to clients.
Instead, use [`MirrorableMap`](TTIVI_INDIGO_API) which is optimized for updates over Binder
transactions. It becomes a regular `Map` on the client side.

```kotlin
@IviService(serviceId = "service.example")
interface ExampleService {
    val mapProp: MirrorableMap<Int, String>
}
```

#### Data sources

The values of IVI service properties are pushed to all clients. When a pull model is desired, for
instance for large data sets, it is possible to instead use a property of type
[`IviDataSource`](TTIVI_INDIGO_API). An `IviDataSource` can be used to expose a data set
to clients without requiring the full data set to be loaded into memory. It also allows querying and
sorting of the data on the service side and allows clients to process the data while it is also
loading it from the service.

To load data from an `IviDataSource` you need to create one or more
[`IviPagingSource`](TTIVI_INDIGO_API)s. The `IviPagingSource`s can be used to load data pages from
the data source. Each `IviPagingSource` is bound to a query.

The `IviPagingSource` class is designed to seamlessly integrate with the
[Android Paging library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview).
This makes it possible to represent elements in a
[`RecyclerView`](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView).
The [`platform_framework_api_ipc_iviserviceandroidpaging`](TTIVI_INDIGO_API) module contains
extension functions for the integration.

```kotlin
@IviService(serviceId = "com.example.ivi.example.plugin.service")
interface AccountsService {
    @IviExperimental
    val accounts: IviDataSource<Account, AccountsDataSourceQuery>
}
```

If you want to use an IVI data source, follow the
[use an IVI data source](/tomtom-indigo/documentation/tutorials-and-examples/basics/use-an-ivi-data-source)
tutorial.

__Note:__ `IviDataSource` is an experimental API.

## The IVI service implementation

### Behind the scenes

The IVI service framework generates multiple classes and methods from an annotated interface using
the [kapt](https://kotlinlang.org/docs/kapt.html) compiler plugin. They all belong to the package of
the service interface. These classes include an abstract base class for the service implementation,
client's API, and service connections.

### Project configuration

An IVI service implementation should be defined in a different package than the interface.

To implement the service interface, create a new module at `examples/plugin/service/` and add a
build script.

Create `build.gradle.kts`:

```kotlin
dependencies {
    // The IVI service interface project provides the base class for an implementation.
    implementation(project(":examples_plugin_serviceapi"))
}
```

The IVI service implementation project is an Android project, so it must have
an `AndroidManifest.xml`file.

Create `src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest package="com.example.ivi.example.plugin.service" />
```

### The service implementation

The IVI service framework generates an abstract base class for the implementation of the service
API. The generated name is of the format `<ServiceInterface>Base`, in our
example `AccountsServiceBase`. The service implementation must derive the class and implement the
methods defined in the service interface.

Create `src/main/kotlin/com/example/ivi/example/plugin/service/StockAccountsService.kt`

```kotlin
package com.example.ivi.example.plugin.service

import com.example.ivi.example.plugin.common.Account
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

internal class StockAccountsService(iviServiceHostContext: IviServiceHostContext) :
    AccountsServiceBase(iviServiceHostContext) {

    override fun onCreate() {
        super.onCreate()

    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun logIn(username: String, password: String): Boolean {
        // Implement functionality
    }

    override suspend fun logOut() {
        // Implement functionality
    }

    companion object {
        private fun isValidUsername(value: String) = value.trim().length > 1
        private fun isValidPassword(value: String) = value.trim().length > 2
    }
}
```

The service implementation must initialize all properties of the service interface at a startup. 
The service has observers for the properties to propagate changes to the clients. The lifecycle of
observers depends on the service's lifecycle. Therefore, the service's lifecycle has to be started
to propagate value changes. The implementation of `onCreate` callback in the base class starts the
service's lifecycle. The service implementation should prefer overriding the `onCreate` callback 
for the initialization and avoid the `init` block. The overridden `onCreate()` method must call the
super method. The `init` block can be still used to initialize private properties of the
implementation that are not defined in the service interface and do not depend on the service's
lifecycle.

__Note:__ After the service is created, it is not yet available for clients until it declares its
availability by changing the __`serviceReady`__ property to `true`.

The `serviceReady` property can be set in the `onCreate` when all properties have been
initialized. If the property has deferred initialization, because it depends on a LiveData value of
another service for example, the availability can be changed via `onRequiredPropertiesInitialized`.
This function is called when all (required) non-nullable properties are initialized with values.
This is the preferred way even if there is no deferred initialization, or no required properties.

The implementation of the service methods is similar to the implementation of a regular Kotlin
interface. It can declare new properties although they will not be visible in the service API
interface.

## Service deployment

An IVI service can be deployed in binder and direct mode. In binder mode the service runs as an
Android service in a separate process. In direct mode the service runs in a thread of the main
application. Binder mode is preferred and used by default.

Direct mode should be used if and only if binder mode cannot be used. For example, if a service 
must run in the main process due to strict performance requirements or functional limitations.

The deployment configuration is defined in the build script of the main or test application module.

### Create a service host

An IVI service needs a host to run. A host may implement multiple service interfaces. The IVI
service framework provides the abstract builder class `IviServiceHostBuilder` to build a service
host. There is also the generic implementation of a service host that takes a list of service
interface implementations. The generic implementation can be created by implementing the
[`SimpleIviServiceHostBuilder`](TTIVI_INDIGO_API).

The builder class must at least contain an empty companion object, which will be extended by the
IVI service framework. Kotlin does not allow extending the companion object if it has not been
declared.

The builder class must follow a specific naming convention. It must have a "ServiceHostBuilder"
suffix and must start with an upper case character.

Create `src/main/kotlin/com/example/ivi/example/plugin/service/AccountsServiceHostBuilder.kt`:

```kotlin
package com.example.ivi.example.plugin.service

import com.tomtom.ivi.platform.framework.api.ipc.iviservice.AnyIviServiceBase
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext
// The simple implementation of a service host builder.
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.SimpleIviServiceHostBuilder

// `ServiceHostBuilder` suffix is mandatory.
class AccountsServiceHostBuilder : SimpleIviServiceHostBuilder() {

    override fun createIviServices(
        iviServiceHostContext: IviServiceHostContext
    ): Collection<AnyIviServiceBase> =
        // Return the service interface implementation to run in the host.
        listOf(StockAccountsService(iviServiceHostContext))

    // The builder implementation must at least contain an empty companion object, which will be
    // extended by the IVI service framework.
    // Kotlin does not allow extending the companion object if it has not been declared.
    companion object
}
```

### Configure the deployment

Define an IVI service host implementation, in your gradle file. This can also be defined in a 
top-level gradle file (e.g. `iviservicehosts.gradle.kts`) so it can be used in a 
multi-project build, including the tests.

Modify `examples/plugin/app/build.gradle.kts`:

```kotlin
import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceInterfaceConfig

/**
 * Defines a configuration for the accounts service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */
val accountsServiceHost =
    IviServiceHostConfig(
        // Needs to match with the name of the builder class.
        serviceHostBuilderName = "AccountsServiceHostBuilder",
        // The module with the implementation of the service host builder class.
        implementationModule = ExampleModuleReference("examples_plugin_service"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AccountsService",
                // The module with the service interface.
                serviceApiModule = ExampleModuleReference("examples_plugin_serviceapi")
            )
        ),
        dependencies = IviServiceDependencies(required = accountSettingsServiceHost.interfaces)
    )
```

The service host build configuration uses the `ExampleModuleReference` class to resolve a module
name into the fully-qualified package. It is defined once and used for all configurations. See
[Integrate TomTom IndiGO into a Gradle Project](/tomtom-indigo/documentation/tutorials-and-examples/setup/integrate-tomtom-indigo-into-a-gradle-project#module-references)
for details.

Register the service host build configuration in the main application's build script.

Modify `examples/plugin/app/build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

ivi {
    application {
        enabled = true
        services {
            // Register the accounts service to the application.
            addHost(accountsServiceHost)
        }
    }
}

// The rest of the build script, dependencies, etc.
```

## Use the IVI service in the client side code

Clients access a service via an instance of the `<ServiceInterface>Api` class, in our
example, `AccountsServiceApi`. The API instance is created with `<ServiceInterface>.createApi(...)`
or `<ServiceInterface>.createApiOrNull(...)`. The former requires a service to be registered and
running, and the latter is an optional connection.

To use the service API, create an instance with `createApi` in the view-model of the account login
page of the account frontend.

In `examples/plugin/frontend/` create: 
`src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginViewModel.kt`:

```kotlin
package com.example.ivi.example.plugin.frontend.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.ivi.example.plugin.serviceapi.AccountsService
import com.example.ivi.example.plugin.serviceapi.createApi
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FrontendViewModel
import com.tomtom.tools.android.api.livedata.allTrue
import com.tomtom.tools.android.api.livedata.valueUpToDate

internal class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    // Create an instance of service's client API.
    private val accountsServiceApi =
        AccountsService.createApi(this, frontendContext.iviServiceProvider)

    // Properties used by the layout.
    val username = MutableLiveData("")
    val password = MutableLiveData("")

    // [allTrue] is a LiveData transformation that emits `true` when all sources become `true`,
    // and `false` otherwise.
    val isLoginEnabled = allTrue(
        // Wait till the service becomes available.
        accountsServiceApi.serviceAvailable,
        username.map { it.isNotBlank() },
        password.map { it.isNotBlank() }
    )

    fun onLoginClick() {
        // `LiveData.valueUpToDate` returns the current value if it is available. 
        // Unlike `LiveData.value` it uses an observer to obtain the value, so it can be used
        // with LiveData's transformations.
        isLoginEnabled.valueUpToDate?.takeIf { it }?.let {
            val username = username.value ?: return
            val password = password.value ?: return

            // Log in an user with asynchronous call.
            accountsServiceApi.logInAsync(username, password)

            // There is a suspendable method as well.
            // runBlocking {
            //     accountsServiceApi.coLogIn(username, password)
            // }
        }
    }
}
```

The service's client API is similar to the service interface. Service properties are mirrored in 
the API instance as `LiveData` of the original type. For example,`val activeAccount: Account?` in 
the service interface becomes `val activeAccount: LiveData<Account?>`. Service methods are mapped 
to two execution models:
as [coroutine](https://kotlinlang.org/docs/async-programming.html#coroutines) `coLogIn(...)` and
async call `logInAsync(...)`.

The `accountsServiceApi.serviceAvailable` property mirrors the `serviceReady` property of the 
service implementation.

Create `src/main/kotlin/com/example/ivi/example/plugin/frontend/info/AccountInfoViewModel.kt`:

```kotlin
package com.example.ivi.example.plugin.frontend.info

import androidx.lifecycle.map
import com.example.ivi.example.plugin.serviceapi.AccountsService
import com.example.ivi.example.plugin.serviceapi.createApi
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FrontendViewModel
import java.util.Locale

internal class AccountInfoViewModel(panel: AccountInfoPanel) :
    FrontendViewModel<AccountInfoPanel>(panel) {

    private val accountsServiceApi =
        AccountsService.createApi(this, frontendContext.iviServiceProvider)

    val displayName = accountsServiceApi.activeAccount.map {
        it?.username?.replaceFirstChar(Char::uppercaseChar)
    }

    fun onLogoutClick() = accountsServiceApi.logOutAsync()
}
```

## Sharing data between service and clients

All properties of an IVI service are mirrored.
