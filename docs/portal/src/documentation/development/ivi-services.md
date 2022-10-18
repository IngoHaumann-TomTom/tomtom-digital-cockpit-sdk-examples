---
title: IVI Services
---

An IVI system is made up of IVI services which can provide a UI or another service with information.
TomTom IndiGO services provide a mechanism to encapsulate longer running tasks and business logic
for some distinct functionality in the platform. They don't contain a User Interface (UI) of their
own and can run in the background for a long time, matching the platform lifetime if required.

## TomTom IndiGO system

A TomTom IndiGO based IVI system is deployed as a single Android application that includes all the
functionality of the product. The User Interface (UI) of the application is built using TomTom
IndiGO [frontend plugins](/tomtom-indigo/documentation/development/frontend-plugins), and are created to
display something on the screen. They are subsequently destroyed when content disappears from the
screen. Frontend plugins UI interaction must run on a single thread (_the UI thread_), as dictated
by the Android platform. Whereas IVI services should contain functionality that does not need to
(or should not) run on the UI thread. IVI services are hosted in service hosts and each service host
instance will host at least one IVI service implementation. Service hosts may be configured to run
in separate processes, therefore IVI service implementations may as well run in separate processes.

### Discoverable services

In a deployed TomTom IndiGO product, a _discoverable service_ can have any number of implementations
(whereas a non _discoverable service_ can only have one implementation of each service interface).
Clients of a discoverable service interface can discover all the implementations. This allows the
adding of many implementations of a defined interface. An example of this is a
MessagingProviderService that can for example be implemented for SMS but also for any other
messaging technology.

### Android services

TomTom IndiGO services hosts are built on
[Android Services](https://developer.android.com/guide/components/services), and use the same
mechanisms, but hide some of the details of the Android service implementation. For example, you
define the interface to a service directly in Kotlin, and to communicate with it there is no need
to provide an `IBinder`, use a `Messenger`, or define an AIDL interface. In addition, the binding
is done automatically, and the service is ready to use when the `serviceReady` flag has been set.

By default, Android services run in the main thread in the main process, whereas an TomTom IndiGO
service by default starts and runs in a different process.

As service hosts are using the Android Binder framework to transfer data, they have the same
limitations as an Android bound service, therefore the data types used in a service's interface
can only be primitives, or a type that implements the
[`Parcelable` interface](https://developer.android.com/reference/android/os/Parcelable).

### Service host lifecycle

A service host is configured and registered in the build configuration of the IVI product, but
only started when a client is using it. A client starts using a service host by calling
`createApi` and when a service host is created, the `onCreate` callback is called, which starts
the service host's lifecycle. The service implementation must initialize all properties of the
service interface at startup, and at this point the service can initialize any uninitialized
properties and set itself available with the `serviceReady` property. If the service has deferred
initialization, the availability can be changed in the `onRequiredPropertiesInitialized` callback
instead. Note that the service is not available for clients to use until the `serviceReady`
property is set to `true`. The service host stays alive as long as there is a client using it,
that is, as long as any of the `lifecyleOwner` passed to `createApi` is active.

## How does it work?

The TomTom IndiGO build system generates multiple classes and methods from the service interface
definition based on the annotated interfaces. They all get generated in the same package as the
service interface, these classes include an abstract base class for the service host
implementation, client's API, and the service IPC logic.

The interface to a service should be defined in a separate module, so both the service host
implementation and the client can use it.

__Example__

Define a service with an interface like this:

```kotlin
@IviService(
    serviceId = "com.example.ivi.example.service.exampleservice"
)
interface ExampleService {

    @IviServiceFun
    suspend fun myServiceApi(someParam: String)

    val timeStamp: Long

    // A companion object is needed for the generated code (as a minimum
    // an empty object needs to be supplied).
    companion object
}
```

The interface itself is annotated with [`@IviService`](TTIVI_PLATFORM_API) and any API methods for
the service should be annotated with [`@IviServiceFun`](TTIVI_PLATFORM_API). Based on this, the
build system generates the necessary classes and methods to create an implementation.

An abstract base class is generated for the implementation of the service host to derive from. The
format is the "interface name" + `Base`, in our example `ExampleServiceBase`.

The API methods can now be implemented, and once all initialization has been done, the service
host can set itself to available, and ready to use.

```kotlin
class StockExampleService(iviServerContext: IviServerContext) :
    ExampleServiceBase(iviServerContext) {

		override fun onCreate() {
        super.onCreate()
        // ...

        // once all initialisation has been done, set the service to ready.
		serviceReady = true
    }

    override suspend fun myServiceApi(someParam: String) {
        // ...
    }
}
```

On the client side, the service API can then be created with a simple call to the generated
createApi() method:

```kotlin
// Create an instance of service's client API.
private val exampleServiceApi =
    ExampleService.createApi(this, frontendContext.iviServiceProvider)
```

All the properties of the service are mirrored in the API instance, as
[`LiveData<>`](https://developer.android.com/reference/androidx/lifecycle/LiveData) of the
original type, so for example a `Long` in the service:

```kotlin
val timeStamp: Long
```

becomes a `LiveData<Long>` in the service interface:
```kotlin
val timeStamp: LiveData<Long>
```

### Calling service methods

The actual methods are mapped to both a synchronous and an asynchronous version. The synchronous
method is using [coroutines](https://kotlinlang.org/docs/async-programming.html#coroutines) and
are prepended with `co`, the asynchronous method is appended with `Async`.
These methods are exposed in the `<Interface>Api` as follows.

```kotlin
// Asynchronous
// When not 'null', 'onResult' is invoked when the service function execution
// completes with that result or when the execution failed.
fun exampleMethodAsync(..., onResult = null)

// Suspendable
// This allows the function to be called from a Kotlin coroutine.
// The function throws an exception when the execution failed.
suspend fun coExampleMethod(...): <ReturnType>
```

Once the service has set itself ready to be used, the client can directly call the APIs. The following
example is using a TomTom IndiGO utility method to call the API once the service is available.

```kotlin
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.queueOrRun

exampleServiceApi.queueOrRun { service ->
    service.myServiceApiAsync("Test")
}
```

__Discoverable service__

To create a discoverable service, the interface should instead be annotated with the
[`@IviDiscoverableService`](TTIVI_PLATFORM_API) annotation. In addition to the `createApi` generated
method, a `createApis` method is also generated that creates all instances for all registered
services for this interface.

## More information

For more details on how to create a IVI service, see the
[Create an IVI Service](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-an-ivi-service)
page, and for more details on how to create a frontend, see the
[Create a Frontend Plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
page.
See
[Configure the Runtime Deployment of the IVI System](/tomtom-indigo/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system)
for information about how to deploy IVI services.
