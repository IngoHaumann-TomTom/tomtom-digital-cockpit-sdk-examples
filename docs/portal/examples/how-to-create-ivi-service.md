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
3. [Run the service as an Android service](#run-service-as-an-android-service).
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
    // extended with a few methods by the IVI service framework.
    // Kotlin does now allow to extend the companion object if it has not been declared.
    companion object
}
```

The interface may have nullable or non-nullable values, methods, and event listener interfaces. All
methods must have the `suspend` modifier and the `IviServiceFun` annotation. The `IviServiceFun`
annotation is required to distinguish methods of the service interface from auxiliary methods added
by Kotlin/Java compiler. The interface must at least contain the empty companion object, which will
be extended by the IVI service framework with a few methods, such as `createApi`
and `createApiOrNull`.

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

    // Provides the base class for Android server to run the service in a separate process.
    implementation(Libraries.TomTom.Indigo.API_FRAMEWORK_IVISERVICEMANAGER)
}
```

The IVI service implementation project is an Android project, so it must have
an `AndroidManifest.xml`file. The IVI service should not run from the main/UI thread, so it can be
pushed to a separate process to run as an Android service in a dedicated process. To setup an
Android service, declare an application with a service in the manifest.

The `android:name` attribute specifies the application class that will be declared later.

The `android:process` attribute is the name of a process where the server should run. By setting
this attribute to a process name that's shared with another application, multiple servers can be run
in the same process. For more information about `android:process`
see [Android documentation](https://developer.android.com/guide/topics/manifest/application-element#proc)
.

**src/main/AndroidManifest.xml**

```xml

<manifest package="com.tomtom.ivi.example.service.account"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <application>
        <service android:name=".AndroidServer" android:process=":server.account" />
    </application>
</manifest>
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

## Run service as an Android service

Android service requires an application class to run. The IVI service framework provides a base
class `IviAndroidServer` for a server application. It has an abstract factory
method `createIviServer` for an IVI server. The framework provides the `SimpleIviServiceServer` with
basic server functionality.

**src/main/kotlin/com/tomtom/ivi/example/service/account/AndroidServer.kt**

```kotlin
package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.api.framework.iviservice.IviServerBase
import com.tomtom.ivi.api.framework.iviservice.IviServerContext
import com.tomtom.ivi.api.framework.iviservice.SimpleIviServiceServer
import com.tomtom.ivi.core.framework.iviservicemanager.IviAndroidServer

class AndroidServer : IviAndroidServer() {
    override fun createIviServer(iviServerContext: IviServerContext): IviServerBase =
        SimpleIviServiceServer(setOf(StockAccountService(iviServerContext)))
}
```

### Register the IVI server

To run the server and its services, the server has to be registered in the main application.

First, add dependencies for the main application at `modules/products/exampleapp`.

**build.gradle.kts**

```kotlin
dependencies {
    // Other application dependencies.

    implementation(project(":serviceapis_account"))
    runtimeOnly(project(":services_account"))
}
```

The service's registration requires a connection class, which is generated by the IVI service
framework in the `serviceapis_account` package. Then there is a runtime dependency to the service
implementation with the Android service.

Then, register a binder connection to the account service in the main application
class (`ExampleApplication`).

**src/main/kotlin/com/tomtom/ivi/example/product/exampleapp/ExampleApplication.kt**

```kotlin
package com.tomtom.ivi.example.product.exampleapp

import com.tomtom.ivi.api.defaults.application.DefaultApplication
import com.tomtom.ivi.api.framework.iviservice.IviServerId
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBinderConnection

class ExampleApplication : DefaultApplication() {
    override fun registerBinderServers() {
        super.registerBinderServers()

        with(serviceManager) {
            registerServerManager(
                createBinderServerManager(
                    IviServerId("com.tomtom.ivi.example.service.account.AndroidServer"),
                    AccountServiceBinderConnection.createRegistration()
                )
            )
        }
    }
}
```

To register the service, override `DefaultApplication.registerBinderServers`. Call the super method
to register default IVI services. Create a new server manager for a binder server, where
`IviServerId` takes the full-qualified class name of the server application, declared
above.`AccountServiceBinderConnection` class is a generated by the IVI service framework for IPC
communications using Android Binder.

## Use the IVI service in the client side code

Clients access a service via an instance of the `<ServiceInterface>Api` class, in our
example, `AccountServiceApi`. The API instance is created with `<ServiceInterface>.createApi(...)`
or `<ServiceInterface>.createApiOrNull(...)`. The former requires a service to be registered and
running, and the latter is an optional connection.

To use the service API, create an instance with `createApi` in the viewmodel of the account login
page of the account frontend.

**src/main/kotlin/com/tomtom/ivi/example/frontend/account/login/AccountLoginViewModel.kt**

```kotlin
package com.tomtom.ivi.example.frontend.account.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.tools.android.core.livedata.allTrue
import com.tomtom.tools.android.core.livedata.valueUpToDate

class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    // Create an instance of service's client API.
    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    // Properties used by the layout.
    val username = MutableLiveData("")
    val password = MutableLiveData("")

    // [allTrue] is a LiveData transformation that emits `true` on all sources becoming `true`.
    val isLoginEnabled = allTrue(
        // Wait till the service becomes available.
        accountServiceApi.serviceAvailable,
        username.map { it.isNotBlank() },
        password.map { it.isNotBlank() }
    )

    fun onLoginClick() {
        // `LiveData.valueUpToDate` returns the current value if it is available. 
        // Unlike `LiveData.value` it uses an observer to obtain the value, so it can be used with LiveData's transformations.
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

The service's client API is very similar to the service interface. Service properties are mirrored
in the API instance as `LiveData` of the original type. For example,`val activeAccount: Account?` in
the service interface becomes `val activeAccount: LiveData<Account?>`. Service methods are mapped
with two execution models:
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