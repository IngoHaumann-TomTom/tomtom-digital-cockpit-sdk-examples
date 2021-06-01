# How to create an IVI service

## Introduction for IVI application architecture

An IVI application consists of IVI services for the business logic, and frontends for UI, which
combines layouts, panels, and view models. Panels and associated view models are rather short-living
components that are created to display something on screen, and are destroyed as soon as content
disappears. Services live much longer, almost matching the application lifetime. Apart from
Application Platform services, there can be application services that work with dedicated frontends.
Such services may suit as a model for a frontend.

## Overview of the example application

The example application adds the account frontend for the default IndiGO application. The account
frontend adds new panels to show account information or login page, which can be invoked by a menu
item. Account status is managed by the account service. The source code for the frontend and service
can be found at [modules](/modules).

## The plan

The account service will provide methods to login and logout a user, and keep the current logged
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

To define the service interface, first create a new module at `modules/serviceapis/account` and add
a build script.

**build.gradle.kts**

```kotlin
import com.tomtom.ivi.api.gradle.plugin.extensions.ivi

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

**src/main/AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest package="com.tomtom.ivi.example.serviceapi.account" />
```

### The IVI service interface

When the project is configured, create a Kotlin `interface` class, annotated with the `IviService`
annotation, which takes one mandatory argument `serviceId` which specifies the unique identifier
that is used by client connections.

**src/main/kotlin/com/tomtom/ivi/example/serviceapi/account/AccountService.kt**

```kotlin
// The annotation for an IVI service interface.
import com.tomtom.ivi.api.framework.iviserviceannotations.IviService
// The annotation for IVI service functions.
import com.tomtom.ivi.api.framework.iviserviceannotations.IviServiceFun

@IviService(
    serviceId = "com.tomtom.ivi.example.service.account"
)
interface AccountService {
    val activeAccount: Account?

    @IviServiceFun
    suspend fun logIn(username: String, password: String): Boolean

    @IviServiceFun
    suspend fun logOut()

    // The service interface must at least contain the empty companion object, which will be
    // extended by the IVI service framework.
    // Kotlin does not allow extending the companion object if it has not been declared.
    companion object
}
```

The interface may have nullable or non-nullable values, methods, and event listener interfaces. All
methods must have the `suspend` modifier and the `IviServiceFun` annotation. The `IviServiceFun`
annotation is required to distinguish methods of the service interface from auxiliary methods added
by Kotlin/Java compiler. The interface must at least contain the empty companion object, which will
be extended by the IVI service framework with a few methods, such as `createApi`
and `createApiOrNull`. Kotlin does not allow extending the companion object if it has not been
declared.

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
Instead, use `MirrorableMap` which is optimized for updates over Binder transactions. It becomes a
regular `Map` on the client side.

```kotlin
@IviService(serviceId = "service.example")
interface ExampleService {
    val mapProp: MirrorableMap<Int, String>
}
```

## The IVI service implementation

### Behind the scenes

The IVI service framework generates multiple classes and methods from an annotated interface using
the [kapt](https://kotlinlang.org/docs/kapt.html) compiler plugin. They all belong to the package of
the service interface. These classes include an abstract base class for the service implementation,
client's API and service connections.

### Project configuration

An IVI service implementation should be defined in a different package than the interface.

To implement the service interface, create a new module at `modules/services/account` and add a
build script.

**build.gradle.kts**

```kotlin
dependencies {
    // The IVI service interface project provides the base class for an implementation.
    implementation(project(":serviceapis_account"))
}
```

The IVI service implementation project is an Android project, so it must have
an `AndroidManifest.xml`file.

**src/main/AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest package="com.tomtom.ivi.example.service.account" />
```

### The service implementation

The IVI service framework generates an abstract base class for the implementation of the service
API. The generated name is of the format `<ServiceInterface>Base`, in our
example `AccountServiceBase`. The service implementation must derive the class and implement the
methods defined in the service interface.

**src/main/kotlin/com/tomtom/ivi/example/service/account/StockAccountService.kt**

```kotlin
package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.api.framework.iviservice.IviServerContext
import com.tomtom.ivi.example.serviceapi.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountId
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBase

class StockAccountService(iviServerContext: IviServerContext) :
    AccountServiceBase(iviServerContext) {

    override fun onCreate() {
        super.onCreate()

        activeAccount = null
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun logIn(username: String, password: String): Boolean =
        if (isValidUsername(username) && isValidPassword(password)) {
            val accountId = AccountId(username)
            activeAccount = Account(accountId, username)

            true
        } else {
            false
        }

    override suspend fun logOut() {
        activeAccount = null
    }

    companion object {
        private fun isValidUsername(value: String) = value.trim().length > 1
        private fun isValidPassword(value: String) = value.trim().length > 2
    }
}
```

The service implementation must initialize all properties of the service interface at a startup. The
service has observers for the properties to propagate changes to the clients. The lifecycle of
observers depends on the service's lifecycle. Therefore, the service's lifecycle has to be started
to propagate value changes. The implementation of `onCreate` callback in the base class starts the
service's lifecycle. The service implementation should prefer overriding the `onCreate` callback for
the initialization and avoid the `init` block. The overridden `onCreate()` method must call the
super method. The `init` block can be still used to initialize private properties of the
implementation that are not defined in the service interface and do not depend on the service's
lifecycle.

In the given example, nullable property `activeAccount` is set to `null` by default, and there is no
need to initialize it in `onCreate`. Although it is good for readability to initialize all the
properties explicitly.

After the service is created, it is not yet available for clients until it declares its availability
by changing the `serviceReady` property. It can be set in the `onCreate` when all properties are
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

Direct mode should be used if and only if binder mode cannot be used. For example, if a service must
run in the main process due to strict performance requirements or functional limitations.

The deployment configuration is defined in the build script of the main or test application module.

### Create a service host

An IVI service needs a host to run. A host may run multiple services. The IVI service framework
provides the abstract builder class `IviServiceHostBuilder` to build a service host. There is also
the generic implementation of a service host that takes a list of service interface implementations.
The generic implementation can be created by implementing the `SimpleIviServiceHostBuilder`.

The builder class must at least contain the empty companion object, which will be extended by the
IVI service framework. Kotlin does not allow extending the companion object if it has not been
declared.

The builder class need to have the mandatory "ServiceHostBuilder" suffix to allow it to be used
by the generated deployment code.

**src/main/kotlin/com/tomtom/ivi/example/service/account/AccountServiceHostBuilder.kt**

```kotlin
package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
// The simple implementation of a service host builder.
import com.tomtom.ivi.api.framework.iviservice.SimpleIviServiceHostBuilder

// `ServiceHostBuilder` suffix is mandatory.
class AccountServiceHostBuilder : SimpleIviServiceHostBuilder() {

   override fun createIviServices(iviServiceHostContext: IviServiceHostContext) =
       // Return the service interface implementation to run in the host.
       listOf(StockAccountService(iviServiceHostContext))

    // The builder implementation must at least contain the empty companion object, which will be
    // extended by the IVI service framework.
    // Kotlin does not allow extending the companion object if it has not been declared.
    companion object
}
```

### Configure deployment

Define a deployment configuration for a service host in the `buildSrc` so it can be used in all
projects, including tests.

**buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/config/services/AccountService.kt**

```kotlin
package com.tomtom.ivi.buildsrc.config.services

import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.gradle.api.common.iviapplication.config.IviServiceInterfaceConfig

/**
 * Defines a configuration for the account service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */
val accountServiceHost = IviServiceHostConfig(
    // Needs to match with the name of the builder class.
    serviceHostBuilderName = "AccountServiceHostBuilder",
    // The module with the implementation of the service host builder class.
    implementationModule = ExampleModuleReference("services_account"),
    interfaces = listOf(
        IviServiceInterfaceConfig(
            serviceName = "AccountService",
            // The module with the service interface.
            serviceApiModule = ExampleModuleReference("serviceapis_account")
        )
    )
)
```

The deployment configuration uses `ExampleModuleReference` that resolves a module name into the
full-qualified package. It is defined once and used for all configurations.
Create `ExampleModuleReference` in `buildSrc`.

**buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/dependencies/ExampleModuleReference.kt**

```kotlin
package com.tomtom.ivi.buildsrc.dependencies

import com.tomtom.ivi.gradle.api.common.dependencies.ModuleReference

class ExampleModuleReference(moduleName: String) : ModuleReference(
    groupName = GROUP_NAME,
    moduleName = moduleName,
    packageName = convertModuleNameToPackageName(moduleName)
) {
    companion object {
        private fun convertModuleNameToPackageName(moduleName: String): String {
            val hierarchy = moduleName.split("_", limit = 2)

            val category = hierarchy[0].let {
                when (it) {
                    "services" -> "service"
                    "serviceapis" -> "serviceapi"
                    else -> it
                }
            }
            val module = hierarchy[1]
            return "$GROUP_NAME.$ROOT_NAME.$category.$module"
        }

        private const val ROOT_NAME = "example"
        private const val GROUP_NAME = "com.tomtom.ivi"
    }
}
```

The `ExampleModuleReference.convertModuleNameToPackageName` resolves a module's name
like `serviceapis_account` into `com.tomtom.ivi.example.serviceapi.account`.

Then create the deployment configuration for the account service host in the main application's
build script.

**modules/products/exampleapp/build.gradle.kts**

```kotlin
import com.tomtom.ivi.buildsrc.config.services.accountServiceHost
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

ivi {
    application {
        enabled = true
        services {
            // Register the account service to the application.
            addHost(accountServiceHost)
        }
    }
}

// The rest of the build script, dependencies, etc.
```

### Deploy multiple service hosts in the same process

Closely related services may be deployed in the same process to reduce IPC overhead. For example,
the account settings service, which is used by the account service, and the account service may be
deployed together.

File `buildSrc/src/main/kotlin/com/tomtom/ivi/buildsrc/config/services/AccountService.kt` contains
service host configurations for account services, `accountServiceHost`
and `accountSettingsServiceHost`. To deploy them in the same process, add runtime configuration to
the application config.

**modules/products/exampleapp/build.gradle.kts**

```kotlin
import com.tomtom.ivi.buildsrc.config.services.accountServiceHost
import com.tomtom.ivi.buildsrc.config.services.accountSettingsServiceHost
import com.tomtom.ivi.gradle.api.plugin.platform.ivi

ivi {
    application {
        enabled = true
        services {
            // Register the account service in the application.
            addHost(accountServiceHost)
            // Register the account settings service in the application.
            addHost(accountSettingsServiceHost)
        }
        runtime {
            deployments {
                create(globalRuntime) {
                    useDefaults()

                    // Deploys the account and account settings services in the same process.
                    deployServiceHosts(inList(accountServiceHost, accountSettingsServiceHost))
                        .withProcessName("accountservicehost")
                }
            }
        }
    }
}

// The rest of the build script, dependencies, etc.
```

Now both hosts run in the same `accountservicehost` process.

## Use the IVI service in the client side code

Clients access a service via an instance of the `<ServiceInterface>Api` class, in our
example, `AccountServiceApi`. The API instance is created with `<ServiceInterface>.createApi(...)`
or `<ServiceInterface>.createApiOrNull(...)`. The former requires a service to be registered and
running, and the latter is an optional connection.

To use the service API, create an instance with `createApi` in the view-model of the account login
page of the account frontend.

**src/main/kotlin/com/tomtom/ivi/example/frontend/account/login/AccountLoginViewModel.kt**

```kotlin
package com.tomtom.ivi.example.frontend.account.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.tools.android.api.livedata.allTrue
import com.tomtom.tools.android.api.livedata.valueUpToDate

class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    // Create an instance of service's client API.
    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    // Properties used by the layout.
    val username = MutableLiveData("")
    val password = MutableLiveData("")

    // [allTrue] is a LiveData transformation that emits `true` when all sources become `true`,
    // and `false` otherwise.
    val isLoginEnabled = allTrue(
        // Wait till the service becomes available.
        accountServiceApi.serviceAvailable,
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
            accountServiceApi.logInAsync(username, password)

            // There is a suspendable method as well.
            // runBlocking {
            //     accountServiceApi.coLogIn(username, password)
            // }
        }
    }
}
```

The service's client API is similar to the service interface. Service properties are mirrored in the
API instance as `LiveData` of the original type. For example,`val activeAccount: Account?` in the
service interface becomes `val activeAccount: LiveData<Account?>`. Service methods are mapped to two
execution models:
as [coroutine](https://kotlinlang.org/docs/async-programming.html#coroutines) `coLogIn(...)` and
async call `logInAsync(...)`.

The `accountServiceApi.serviceAvailable` property mirrors the `serviceReady` property of the service
implementation.

**src/main/kotlin/com/tomtom/ivi/example/frontend/account/info/AccountInfoViewModel.kt**

```kotlin
package com.tomtom.ivi.example.frontend.account.info

import androidx.lifecycle.map
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import java.util.*

class AccountInfoViewModel(panel: AccountInfoPanel) : FrontendViewModel<AccountInfoPanel>(panel) {

    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    val displayName = accountServiceApi.activeAccount.map { it?.username?.capitalize(Locale.ROOT) }

    fun onLogoutClick() = accountServiceApi.logOutAsync()
}
```

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.
