---
title: Create a Custom Alexa Handler Service
---

<Blockquote type="announcement" hasIcon>
    <a href="https://developer.tomtom.com/tomtom-indigo/documentation/integrating-tomtom-indigo/integrating-amazon-alexa">
        Additional requirements
    </a>
    &nbsp;exist for using Amazon Alexa in your TomTom IndiGO product.
</Blockquote>

The TomTom IndiGO platform offers a stock implementation of a VPA Adaptation service for the Alexa
Auto VPA, provided by the stock Alexa service.
This service interacts with the Alexa engine by exchanging asynchronous JSON-based messages with
the Alexa Auto Client Service (AACS) application. More information about the AACS is available in
the [Alexa Auto Client Service (AACS)](https://alexa.github.io/alexa-auto-sdk/docs/android/aacs/)
documentation page.

These JSON messages are called "AASB" messages in the Alexa Auto documentation, because they are
used by the Alexa Auto Services Bridge (AASB) Extension.

The AASB messages are grouped into "topics", each representing a domain that requires a platform
implementation. For details about the AASB messages, see the
[AASB message interfaces](https://github.com/alexa/alexa-auto-sdk/blob/4.0/modules/core/README.md#aasb-message-interfaces)
and the [AASB message definitions](https://alexa.github.io/alexa-auto-sdk/docs/aasb/)
pages.

The stock Alexa service of TomTom IndiGO already implements message handlers for many of the topics
offered by Alexa Auto, namely:

- AddressBook
- Alerts
- AlexaClient
- AudioInput
- AudioOutput
- Authorization
- CarControl
- Messaging
- Navigation
- Notifications
- PhoneCallController
- PlaybackController
- PropertyManager
- SpeechRecognizer
- TemplateRuntime

The responsibility of a message handler is:
- To parse AASB messages received by AACS and handle them appropriately (for example, to instruct
  the navigation engine to start navigation when a `StartNavigation` message is received).
- To send "Reply" AASB messages to AACS, reporting whether a request from AACS was successfully
  executed or not.
- To send "Publish" AASB messages to AACS, for example, to send a request to Alexa or to report an
  event.

Some topics, though, are device-specific and cannot be implemented as part of the TomTom IndiGO
platform. The [AlexaHandlerService](TTIVI_INDIGO_API) is a
[discoverable IVI service](/tomtom-indigo/documentation/development/ivi-services#discoverable-services)
interface that can be implemented by an OEM to fulfill one of these purposes:
- handle topics that are not supported by the TomTom IndiGO platform.
- extend or replace some of the existing stock TomTom IndiGO platform message handlers. Currently,
  the only stock TomTom IndiGO platform message handlers that can be extended or replaced with an
  `AlexaHandlerService` implementation are the `CarControl` and the `Navigation` handlers.
  For example, you might want to add support for switching the Cabin Light in the vehicle on and
  off: this is a setting that Alexa Auto supports, but for which the TomTom IndiGO platform's
  CarControl message handler doesn't have built-in support. You could then add this functionality by
  implementing an [AlexaHandlerService](TTIVI_INDIGO_API).

## Example app

The TomTom IndiGO SDK comes with an example app showing how to use the Alexa feature, see directory
`examples/alexa`. By default, this application is excluded from the build, because it requires
Alexa libraries that are not publicly available.

To build the Alexa example app, you should edit the build files:

__gradle.properties:__

```kotlin
optInToAlexaExamples=true
```

Alternatively, you can set this Gradle property from the command-line:

```cmd
./gradlew assemble -PoptInToAlexaExamples=true
```

## How to create a custom Alexa Handler service

The following sections describe how to create a custom Alexa Handler service implementation.

### Create a service implementation module

To implement a custom Alexa Handler service, create a new module under `examples/alexa` (for
example `examples/alexa/customalexahandler`) and add a Gradle build script.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    `kotlinx-serialization`
}

ivi {
    // The AlexaHandlerService API is currently an experimental feature, and has to be explicitly
    // opted in.
    optInToExperimentalApis = true
}

dependencies {
    implementation(libraries.indigoPlatformAlexaApiCommonUtil)
    implementation(libraries.indigoPlatformAlexaApiServiceAlexahandler)
}
```

Declare Alexa dependencies in `/build-logic/libraries.versions.toml` file:

```toml
indigoPlatformAlexaApiCommonUtil = { module = "com.tomtom.ivi.platform:platform_alexa_api_common_util", version.ref = "indigoPlatform" }
indigoPlatformAlexaApiServiceAlexahandler = { module = "com.tomtom.ivi.platform:platform_alexa_api_service_alexahandler", version.ref = "indigoPlatform" }
```

The Alexa Handler service implementation project is an Android project, so it must also have
an `AndroidManifest.xml` file.

__src/main/AndroidManifest.xml:__

```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest package="com.example.ivi.example.alexa.customalexahandler" />
```

### Implement the Alexa Handler service

The Alexa Handler service can be implemented by defining a class (for example,
`CustomAlexaHandlerService`) that inherits from the abstract `AlexaHandlerServiceBase` base class
and implements the methods defined in the [AlexaHandlerService](TTIVI_INDIGO_API) interface.

The service implementation needs to set a number of properties for configuring itself with the
TomTom IndiGO platform. Please refer to the [AlexaHandlerService](TTIVI_INDIGO_API) API reference
documentation for detailed information on these properties.

The properties can be set by overriding the `onCreate()` method. For example:

__src/main/kotlin/com/example/ivi/example/alexa/customalexahandler/CustomAlexaHandlerService.kt__

```kotlin
import com.amazon.aacsconstants.Topic
import com.tomtom.ivi.platform.alexa.api.service.alexahandler.AlexaHandlerService

override fun onCreate() {
    super.onCreate()

    topic = Topic.CAR_CONTROL
    priority = AlexaHandlerService.DEFAULT_HANDLER_PRIORITY
    aacsConfiguration =
        """
        {
          "aacs.carControl" : {
            "endpoints": [
            ...
            ]
          }
        }
        """

    serviceReady = true
}
```

Additionally, the service needs to override a number of methods.
Please refer to the [AlexaHandlerService](TTIVI_INDIGO_API) API reference documentation for
detailed information on these methods.

#### Parsing AASB messages

The Alexa Handler service is able to communicate with the Alexa Auto Client Service by exchanging
JSON based Alexa Auto Services Bridge (AASB) messages.
More information on the format of the AASB messages is available in the
[Alexa Auto AASB message definitions](https://alexa.github.io/alexa-auto-sdk/docs/aasb/)
page.

Whenever the TomTom IndiGO platform receives a JSON AASB message from the Alexa Auto Client Service
which matches with the topic configured in the service implementation, the
[AlexaHandlerService](TTIVI_INDIGO_API).`onMessageReceived` method is called.

The service implementation needs to parse this message and handle it as needed.
Typically, in order to parse an AASB message, you would:

1. Instantiate an instance of the
   [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md)
   JSON parser:

   ```kotlin
   private val jsonParser = Json {
       ignoreUnknownKeys = true
       isLenient = true
       encodeDefaults = true
   }
   ```

2. Define a data class that represents the message to be parsed. For example, the data class
   representing the CarControl
   [SetControllerValue](https://alexa.github.io/alexa-auto-sdk/docs/aasb/car-control/CarControl/#setcontrollervalue)
   message would look like this:

   ```kotlin
   @Serializable
   data class SetControllerValueIncomingMessage(
       val header: Header,
       val payload: SetControllerValueIncomingMessagePayload
   )

   @Serializable
   data class SetControllerValueIncomingMessagePayload(
       val controllerType: String,
       val endpointId: String,
       val turnOn: Boolean
   )
   ```

3. Decode the AASB message into an instance of the data class using the
   [com.tomtom.ivi.platform.alexa.api.common.util](TTIVI_INDIGO_API).`parseAasbMessage` function.

   For example:

   ```kotlin
   override suspend fun onMessageReceived(action: String, messageContents: String
   ): Boolean =
       when (action) {
           Action.CarControl.SET_CONTROLLER_VALUE -> handleSetControllerValue(messageContents)
           else -> false
       }

   private fun handleSetControllerValue(message: String): Boolean {
       val parsedMessage = parseAasbMessage<SetControllerValueIncomingMessage>(
           jsonParser,
           message
       )
       return true
   }
   ```

#### Sending AASB "Reply" messages

In most cases, you will need to send a reply message to AACS to inform Alexa about whether the
request was successfully executed or not.
Typically, in order to send an AASB reply message, you would:

1. Define a data class that represents the message to be sent to AACS. For example, the data class
   representing the CarControl
   [SetControllerValueMessageReply](https://alexa.github.io/alexa-auto-sdk/docs/aasb/car-control/CarControl/#setcontrollervaluereply)
   message would look like this:

   ```kotlin
   @Serializable
   data class SetControllerValueOutgoingMessage(
       val header: Header,
       val payload: SetControllerValueOutgoingMessagePayload
   )

   @Serializable
   data class SetControllerValueOutgoingMessagePayload(
       val success: Boolean
   )
   ```

2. Create an instance of this class, encode it as a JSON string and send it using the
   [AacsSenderWrapper](TTIVI_INDIGO_API) helper class. You would normally also use the
   [com.tomtom.ivi.platform.alexa.api.common.util](TTIVI_INDIGO_API).`createAasbReplyHeader` helper
   function to create the AASB reply header.

   For example:

   ```kotlin
   private fun sendSetControllerValueReply(messageId: String, success: Boolean) {
       val messageToSend = SetControllerValueOutgoingMessage(
           createAasbReplyHeader(
               messageId,
               Topic.CAR_CONTROL,
               Action.CarControl.SET_CONTROLLER_VALUE
           ),
           SetControllerValueOutgoingMessagePayload(success)
       )
       val aacsSender = AacsSenderWrapper(iviServiceHostContext)
       aacsSender.sendMessage(
           jsonParser.encodeToString(messageToSend),
           Topic.CAR_CONTROL,
           Action.CarControl.SET_CONTROLLER_VALUE
       )
   }
   ```

#### Sending AASB "Publish" messages

Some domains require the handler to proactively send a "Publish" message to AACS, for example, to
send a request to Alexa or report an event. You can create these kinds of messages using the
[com.tomtom.ivi.platform.alexa.api.common.util](TTIVI_INDIGO_API).`createAasbRequestNoPayload` or
[com.tomtom.ivi.platform.alexa.api.common.util](TTIVI_INDIGO_API).`createAasbRequestHeader` helper
functions.
Typically, in order to send an AASB "Publish" message, you would:

1. Define a data class that represents the message to be sent to AACS. For example, the data class
   representing the
   [DoNotDisturbChanged](https://alexa.github.io/alexa-auto-sdk/docs/aasb/alexa/DoNotDisturb/#donotdisturbchanged)
   message would look like this:

    ```kotlin
    @Serializable
    data class DoNotDisturbChangedOutgoingMessage(
        val header: Header,
        val payload: DoNotDisturbChangedOutgoingMessagePayload
    )

    @Serializable
    data class DoNotDisturbChangedOutgoingMessagePayload(
        val doNotDisturb: Boolean
    )
    ```

2. Create an instance of this class, encode it as a JSON string and send it using the
   [AacsSenderWrapper](TTIVI_INDIGO_API) helper class. You would normally also use the
   [com.tomtom.ivi.platform.alexa.api.common.util](TTIVI_INDIGO_API).`createAasbRequestHeader` helper function to
   create the AASB Publish header.

   For example:

   ```kotlin
   private fun sendDoNotDisturbChanged(messageId: String, success: Boolean) {
       val messageToSend = DoNotDisturbChangedOutgoingMessage(
           createAasbRequestHeader(
               messageId,
               Topic.DO_NOT_DISTURB,
               Action.DoNotDisturb.DO_NOT_DISTURB_CHANGED
           ),
           DoNotDisturbChangedOutgoingMessagePayload(value)
       )
       val aacsSender = AacsSenderWrapper(iviServiceHostContext)
       aacsSender.sendMessage(
           jsonParser.encodeToString(messageToSend),
           Topic.DO_NOT_DISTURB,
           Action.DoNotDisturb.DO_NOT_DISTURB_CHANGED
       )
   }
   ```

### Create a service host

Your module will also need to define a service host where the service will be running, as well as
provide a service host builder.
This can be achieved by creating 2 classes.

1. A `CustomAlexaHandlerServiceHost` class:

   __src/main/kotlin/com/example/ivi/example/alexa/customalexahandler/CustomAlexaHandlerServiceHost.kt__

   ```kotlin
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviDiscoverableServiceIdProvider
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostBase
   import com.tomtom.ivi.platform.framework.api.ipc.iviservice.IviServiceHostContext

   /**
    * A [CustomAlexaHandlerService] host server.
    */
   internal class CustomAlexaHandlerServiceHost(
       iviServiceHostContext: IviServiceHostContext,
       iviDiscoverableServiceIdProvider: IviDiscoverableServiceIdProvider
   ) :
       IviServiceHostBase(iviServiceHostContext) {

       override val iviServices = setOf(
           CustomAlexaHandlerService(iviServiceHostContext, iviDiscoverableServiceIdProvider)
       )
   }
   ```

2. A `CustomAlexaHandlerServiceHostBuilder` class:

   __src/main/kotlin/com/example/ivi/example/alexa/customalexahandler/CustomAlexaHandlerServiceHostBuilder.kt__

   ```kotlin
   /**
    * A [CustomAlexaHandlerServiceHost] builder used to build a [CustomAlexaHandlerService]
    * host.
    */
   class CustomAlexaHandlerServiceHostBuilder : IviServiceHostBuilder() {

       override fun build(iviServiceHostContext: IviServiceHostContext): IviServiceHostBase =
           CustomAlexaHandlerServiceHost(iviServiceHostContext) {
               getDiscoverableServiceId(it)
           }

       companion object
   }
   ```

Please ensure that the `CustomAlexaHandlerServiceHostBuilder` class is added to the root of your
module hierarchy.

### Configure the service host deployment

Define an IVI service host implementation in your gradle file, This can also be defined in a
top-level gradle file (for example, `iviservicehosts.gradle.kts`) so it can be used in a
multi-project build, including the tests.

__examples/alexa/iviservicehosts.gradle.kts:__

```kotlin
import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.dependencies.IviPlatformModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceInterfaceConfig

/**
 * Defines a configuration for the custom Alexa Handler service.
 *
 * The configuration specifies the service host implementation and the list of interfaces
 * implemented by this service host.
 */
val customAlexaHandlerServiceHost by extra {
    IviServiceHostConfig(
        serviceHostBuilderName = "CustomAlexaHandlerServiceHostBuilder",
        implementationModule = ExampleModuleReference("services_customalexahandler"),
        interfaces = listOf(
            IviServiceInterfaceConfig(
                serviceName = "AlexaHandlerService",
                serviceId = "com.tomtom.ivi.example.service.customalexahandler",
                serviceApiModule = IviPlatformModuleReference(
                    "platform_alexa_api_service_alexahandler"
                )
            )
        )
    )
}
```

Register the service host build configuration in the main application's build script.

__examples/alexa/app/build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

// Define the service host configs as defined in the top-level `iviservicehosts.gradle.kts` file.
apply(from = rootProject.file("examples/alexa/iviservicehosts.gradle.kts"))

// Use Gradle's extra extensions to obtain the `customAlexaHandlerServiceHost` config as defined in
// the top-level `iviservicehosts.gradle.kts` file.
val customAlexaHandlerServiceHost: IviServiceHostConfig by project.extra

ivi {
    application {
        enabled = true
        services {
            // Add the custom Alexa Handler service host to the application.
            addHost(customAlexaHandlerServiceHost)
        }
    }
}

// The rest of the build script, dependencies, etc.
```

## External links

- TomTom IndiGO [AlexaHandlerService](TTIVI_INDIGO_API)
- [Alexa Auto Client Service (AACS)](https://alexa.github.io/alexa-auto-sdk/docs/android/aacs/)
- [Alexa Auto AASB message interfaces](https://github.com/alexa/alexa-auto-sdk/blob/4.0/modules/core/README.md#aasb-message-interfaces)
- [Alexa Auto AASB message definitions](https://alexa.github.io/alexa-auto-sdk/docs/aasb/)
