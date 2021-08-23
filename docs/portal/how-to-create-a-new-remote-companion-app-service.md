# How to create a new remote companion app service

## Introduction

Companion app services are remote services that run on the companion app. Different services can run
on the companion app at the same time. The IVI system can communicate with these through interfaces
defined by Protobuf definition files. These files are then used to generate code that can be directly
imported by the IVI system and the companion app.

For an overview of the companion app communications architecture look at
[How to communicatie with a companion app](how-to-communicate-with-a-companion-app.html).

To create a new remote companion app service the following steps need to be taken:
* [Define a protobuf service description for your service](#how-to-define-a-new-service-description).
* [Implement the service on the companion app](#how-to-implement-the-service-on-the-companion-app).
* [Connect IVI to the new service](#connecting-to-a-companion-app-service-from-the-ivi-side).

### How to define a new service description

First of all, every service is identified using a unique uuid, which is called the *Service UUID*.
An easy way to generate UUIDs is to go to this website
[UUID Generator](https://www.uuidgenerator.net/).

Next we need the actual service definition. Here is a very simple example:

```protobuf
message ExampleMessageRequest {
  ExampleId id = 1;
  string bar = 2;
}

message ExampleMessageResponse {
  ExampleId id = 1;
  string stuff = 2;
}

service ExampleService {
  option (ttivi.uuid) = "bb4b1b97-a240-4e18-8623-19097e2f5ef7";

  rpc testFunctionCall(ExampleMessageRequest) returns (ExampleMessageResponse) {
    option (ttivi.number) = 1;
  }
}
```

Please look at the
[Protobuf Language Guid](https://developers.google.com/protocol-buffers/docs/proto3) for more
information about how to write Protobuf files.

This is what happens in the above example:
* Two new types are defined (`ExampleMessageRequest` and `ExampleMessageResponse`). These can be
  seen as simple data classes.
* The `ExampleService` defines the actual interface of the service.
* Inside the `ExampleService` its *service UUID* is specified using the `ttivi.uuid` option.
* Finally, a function called `testFunctionCall` is defined. Note that each function has a unique
  function number as defined using the `ttivi.number` option.

The service code generator will create the following Kotlin interface for above service definition.

```kotlin
interface ExampleService {
    suspend fun testFunctionCall(
        message: com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageRequest
    ): com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse
}
```

This interface is used for both peers. On the IVI side an implementation of this interface, called a
*proxy*, is provided for every connected companion app that provides this service. On the companion
app side this interface needs to be implemented to provide the service functionality.

As can be seen, the `testFunctionCall` is generated as *suspend* function with the
`ExampleMessageRequest` as parameter and the `ExampleMessageResponse` as result. When this function
is called on the IVI *proxy* object it will cause an asynchronous protocol message to be sent to the
companion app, after which the called function will suspend. This protocol message will be received
by the companion app and will trigger a call to the same method on the service implementation. When
that function returns (either immediately, or after it has suspended) another protocol message,
containing the return value, will be sent back to the IVI side. This will cause the suspended
function to be resumed, which will make it return the provided return value.

At any point in time the job that is running the suspend function can be canceled. Cancelation will
be propagated to the companion side as well. If the companion side function already returned, it
will just cause the result value to be discarded. If that function did not yet return it will be
canceled.

__Note:__ Canceling an IVI side function call does not cause blocking on the IVI side if the
companion side implementation does not support cancelation.

Next to normal functions, service definitions also support the following special ones:
* [Returning multiple values](#returning-multiple-values).
* [Properties](#properties).
* [Map properties](#map-properties).

### Returning multiple values

The service definition also supports functions that, asynchronously, return multiple values.

Here is an example of such a function:

```protobuf
  rpc testStreamingFunctionCall(ExampleMessageRequest) returns (stream ExampleMessageResponse) {
    option (ttivi.number) = 2;
  }
```

The generated Kotlin interface will look like this:

```kotlin
    suspend fun testStreamingFunctionCall(
        message: com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageRequest,
        resultCallback: (com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse) -> Unit
    ): Unit
```

#### IVI Side
When this function is called, and the protocol message is sent to the companion app, it is
suspended, during which the specified `resultCallback` can be called zero or more times, depending
on the companion side service implementation. Then one of two things can happen:
1) The companion side implementation returns or is canceled, which will cause the IVI side function
   call to resume and return.
2) The suspended function call is canceled. The cancelation is then propagated to the companion app,
   just like the simple function call example.

#### Companion app side
The service implementation for this function should return multiple values asynchronously by calling
`resultCallback` and suspending during or inbetween those calls. Once the function returns or is
canceled it should not call `resultCallback` again.

Example implementation:

```kotlin
    override suspend fun testStreamingFunctionCall(
        message: com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageRequest,
        resultCallback: (com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse) -> Unit
    ) {
        for (i in 1..3) {
            resultCallback(/* ... */)
            delay(1000)
        }
    }
```

This will cause three asynchronous values to be returned.


### Properties

It is also possible to define properties that can be observed for value changes.

Here is an example of such a property:

```protobuf
  rpc testLiveDataProperty(ExampleMessageRequest) returns (stream ExampleMessageResponse) {
    option (ttivi.number) = 3;
    option (ttivi.live_property) = true;
  }
```

As can be seen, a property is defined in the same way as a function that returns multiple values,
except for the addition of the `ttivi.live_property` option.

This will result in the following Kotlin interface:

```kotlin
    val testLiveDataProperty:
        LiveData<com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse?>
```

#### IVI side
On the IVI side this live data property can be observed, like any other live data property.

During the time that the property has at least one observer a multiple return value request is
opened to the companion app. When there are no more observers left, this request is closed.

Example implementation:

```kotlin
    proxy.testLiveDataProperty.observe(lifecycleOwner) { value ->
        // Do something with `value`.
    }
```

#### Companion app side
The implementation should override the property and return a live data object.

Example implementation:

```kotlin
    private val mutableTestLiveDataProperty =
        MutableLiveData<com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse?>()
    override val testLiveDataProperty = mutableTestLiveDataProperty.asLiveData()
```

### Map properties

It is also possible to define map properties that can be observed for value changes.

Here is an example of a map property:

```protobuf
  rpc testLiveDataMapProperty(ExampleEmptyRequest) returns (stream ExampleMessageResponse) {
    option (ttivi.number) = 4;
    option (ttivi.live_property) = true;
    option (ttivi.map_key_field_number) = 1;
  }
```

As can be seen, this is implemented in the same way as normal properties, except for the addition
of the `ttivi.map_key_field_number` option. This option holds the number of the field of the return
type that should be considered the key of the map.

__Note:__ Currently the type of the key field needs to be a custom defined *message*.

This will result in the following Kotlin interface:

```kotlin
    val testLiveDataMapProperty:
        LiveData<Map<com.tomtom.ivi.sdk.communications.exampleservice.ExampleId,
        com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse?>>
```

#### IVI side
This works the same way as the normal properties.

Example implementation:

```kotlin
    proxy.testLiveDataMapProperty.observe(lifecycleOwner) { mapValue ->
        // Do something with `mapValue`.
    }
```

#### Companion app side
The implementation should override the property and return a live data map object.

Example implementation:

```kotlin
    private val mutableTestLiveDataMapProperty = MutableLiveData<
            Map<
                com.tomtom.ivi.sdk.communications.exampleservice.ExampleId,
                com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageResponse?
            >
        >(emptyMap())
    override val testLiveDataMapProperty = mutableTestLiveDataMapProperty.asLiveData()
```

## How to implement the service on the companion app

First you need to implement the service stub. This provides the actual service functionality.

The easiest way to do this is to make this an inner class, so it can reference the outer class. The
reason for this is that we will create it in a factory class later on. In certain transitory
states it is possible that multiple instances of this class will exist.

```kotlin
  val testLiveData = MutableLiveData<ExampleMessageResponse?>()
  val testLiveDataMap = MutableLiveData<Map<ExampleId, ExampleMessageResponse?>>()

  inner class ExampleServiceImpl : ExampleService.Stub() {
    override val testLiveDataProperty = testLiveData
    override val testLiveDataMapProperty = testLiveDataMap

    override suspend fun testFunctionCall(message: ExampleMessageRequest): ExampleMessageResponse {
      // Add implementation.
    }

    override suspend fun testStreamingFunctionCall(
      message: ExampleMessageRequest,
      resultCallback: (ExampleMessageResponse) -> Unit
    ) {
      // Add implementation.
    }
  }
```

Next the service factory needs to be created, which simply returns an instance of the service
implementation. The `ExampleService.serverDescriptor` provides the factory with all the necessary
`ExampleService` metadata.

```kotlin
    class ExampleServiceFactory : CommunicationsServerFactory(ExampleService.serverDescriptor) {
        override fun create(): CommunicationsServiceBase {
            return ExampleServiceImpl()
        }
    }
```

Then the communications server context needs to be created. This just contains the context and a
lifecycleOwner to regulate the services life time. The service factory will provide all the meta
data necessary for the server to know which service to serve (in this case the `ExampleService`).

```kotlin
  val communicationsServerContext = CommunicationsServerContext(
      applicationContext,
      this,
      ExampleServiceFactory()
  )
```

Finally the communications server needs to be created. After this is created the service will be
made available on the companion app.

```kotlin
  val communicationServer = CommunicationsServer(communicationsServerContext)
```

## Connecting to a companion app service from the IVI side

Connecting to a companion app service entails creating a [CommunicationsClient], which will notify a
listener whenever there is a connection with a particular service on a companion app or when that
connection has gone down.

```kotlin
    val communicationsClient = CommunicationsClient(
        communicationsClientContext,
        communicationsServiceClientListener
    )
```

But first you need a [CommunicationsClientContext] which tells the [CommunicationsClient] which
service to try to connect to. In the example we will try to connect to the `ExampleService`. This is
a service that is pre-defined and exported by the communications sdk.

```kotlin
    private val communicationsClientContext = CommunicationsClientContext(
        iviServiceHostContext.context,
        this,
        ExampleService.ClientFactory()
    )
```

Now you need the [CommunicationsClientListener], which will be informed about which services are
available. Every companion app that is connected and which provides the `ExampleService` will cause
a call to the listeners [CommunicationsClientListener.onServiceConnected] function. Vise verca,
every time a connection goes down the listeners [CommunicationsClientListener.onServiceDisconnected]
will be called.

Every available service has a globally unique [ServiceProviderUuid], which will be different
every time a service connection is established.

```kotlin
    private val communicationsServiceClientListener = object : CommunicationsClientListener {
        override fun onServiceConnected(
            serviceProviderUuid: ServiceProviderId,
            client: CommunicationsServiceBase
        ) {
            val proxy = client as ExampleService
            // You can now use `proxy` to call into the remote service.
        }

        // This is called when a connection is lost to a previously connected service.
        override fun onServiceDisconnected(serviceProviderUuid: ServiceProviderId) {
            // From now on the proxy associated with `serviceProviderUuid` is no longer connected.
        }
    }
```

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.
