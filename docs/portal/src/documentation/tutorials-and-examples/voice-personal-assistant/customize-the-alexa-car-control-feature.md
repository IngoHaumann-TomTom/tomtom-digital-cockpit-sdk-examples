---
title: Customize the Alexa Car Control Feature
---

<Blockquote type="announcement" hasIcon>
    <a href="https://developer.tomtom.com/tomtom-indigo/documentation/integrating-tomtom-indigo/integrating-amazon-alexa">
        Additional requirements
    </a>
    &nbsp;exist for using Amazon Alexa in your TomTom IndiGO product.
</Blockquote>

One of the Alexa Auto features that will most likely need customizations for your own vehicle
requirements is Car Control. The Car Control module enables an OEM to build a custom experience
that allows users to use Alexa to voice-control vehicle features.

A full description of the Car Control module and its features is available in the
[Alexa Auto documentation](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control).

TomTom IndiGO offers a stock implementation of a `CarControl` handler service, aimed at
controlling heating, ventilation, and air-conditioning (HVAC) of the vehicle by interacting with the
[VehicleHvacService](TTIVI_INDIGO_API).

To allow an OEM to customize the stock Car Control service implementation (or completely replace
it), the TomTom IndiGO platform offers the possibility of implementing an Alexa handler service for
the `CarControl` topic. For more information on how to create a custom Alexa handler service, see
the
[Create a Custom Alexa Handler Service](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service)
tutorial.

## Example app

The TomTom IndiGO SDK comes with an example Alexa app (see directory `examples/alexa`). The app
includes an example `CustomCarControlHandlerService` (see directory
`examples/alexa/customcarcontrolhandler`), showing:
- How to define custom CarControl endpoints and assets.
- How to handle user requests such as "Turn on the light", "Increase the light brightness" or
  "Switch on my custom device".

To build the Alexa example app, you should edit the build files:

__gradle.properties:__

```kotlin
optInToAlexaExamples=true
```

Alternatively, you can set this Gradle property from the command-line:

```cmd
./gradlew assemble -PoptInToAlexaExamples=true
```

## How to create a custom Car Control handler service

The following sections describe how to create a custom Car Control handler service.

### Create a minimal Custom Car Control handler service module

Follow the steps in
[Create a service implementation module](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service#create-a-service-implementation-module)
to create the initial `CustomCarControl` handler service module and in
[Implement the Alexa Handler service](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service#implement-the-alexa-handler-service)
to implement a minimal `CustomCarControlHandlerService` implementation.

### Define a custom Car Control configuration

The `Car Control` configuration informs the Alexa Auto Engine which vehicle features to advertise to
Alexa for control by the user. More details can be found on the
[Configuring the Car Control Module](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control/#configuring-the-car-control-module)
page.

The stock TomTom IndiGO Alexa Car Control service dynamically generates a Car Control configuration
for a number of endpoints, based on information retrieved from the
[VehicleHvacService](TTIVI_INDIGO_API):
- Air Conditioning
- Climate Control
- Fan
- Vent
- Heater
- Seat Heater

It's possible to extend this stock Car Control configuration with additional endpoints, to handle
voice control of other vehicle features. The custom Car Control configuration will be merged with
the stock Car Control configuration by the TomTom IndiGO platform.

To define a custom Car Control configuration:

1. Add an `aacs_customcarcontrol_config.json` file to the Android assets of your
   `CustomCarControlHandlerService` module (for example
   `src/main/assets/config/aacs_customcarcontrol_config.json`)

2. Define your custom Car Control configuration in the `aacs_customcarcontrol_config.json` file. The
   format of the file is described in the Alexa Auto
   [Configuration Format](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control/#configuration-format)
   page.

3. In the initialization of your `CustomCarControlHandlerService`, read the contents of the
   `aacs_customcarcontrol_config.json` file into the `aacsConfiguration` property.

    __CustomCarControlHandlerService.kt:__

    ```kotlin
    private fun readAacsConfig(context: Context): String? =
        try {
            with(context) {
                val filePath = "config/aacs_customcarcontrol_config.json"
                assets.open(filePath).bufferedReader().use { it.readText() }
            }
        } catch (exception: IOException) {
            tracer.e("AACS configuration file not found.", exception)
            null
        }

    override fun onCreate() {
        ...
        aacsConfiguration = readAacsConfig(context)
        ...
    }
    ```

### Define custom Car Control assets

As explained in the
[Alexa Auto documentation](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control/#assets),
assets, identified by unique IDs, group a voice-accessible friendly name like "air conditioner" into
a named group of synonyms and translations for all supported languages.

The Alexa Auto SDK provides a default
[automotive catalog of assets](https://github.com/alexa/alexa-auto-sdk/blob/master/modules/car-control/assets/assets-1P.json)
which can be used when building the Car Control configuration, but it also allows clients to define
custom assets if they are not already part of the default catalog. This is explained in the
[Additional Notes about Assets](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control/#additional-notes-about-assets).

To define and use custom assets:

1. Add a `custom_assets.json` file to the Android assets of your `CustomCarControlHandlerService`
   module (for example `src/main/assets/config/custom_assets.json`).

2. Add your custom assets definitions to the `custom_assets.json` file. The format of this file must
   follow the same schema as the
   [automotive catalog of assets](https://github.com/alexa/alexa-auto-sdk/blob/master/modules/car-control/assets/assets-1P.json).

3. In the initialization of your `CustomCarControlHandlerService`, copy the `custom_assets.json`
   file to the app's internal storage:

    __CustomCarControlHandlerService.kt:__

    ```kotlin
        override fun onCreate() {
            ...
            // Copy the `custom_assets.json` file from the assets storage to the internal storage, so
            // that it can be found by AACS.
            copyCustomAssets()
            ...
        }

        private fun copyCustomAssets() {
            try {
                with(context) {
                    val customAssetsPath = "config/custom_assets.json"
                    assets.open(customAssetsPath).use { inputFile ->
                        FileOutputStream(filesDir?.resolve("custom_assets.json")).use {
                            inputFile.copyTo(it)
                        }
                    }
                }
            } catch (exception: IOException) {
                tracer.e("Failed to copy custom assets file.", exception)
            }
        }
    ```

4. Add a reference to the `custom_assets.json` file to the `aacs_customcarcontrol_config.json` file.

   __aacs_customcarcontrol_config.json:__

   ```json
   {
     "aacs.localVoiceControl": {
       "CarControl": {
         "CustomAssetsFilePath": "/data/user/10/com.example.ivi.example.alexa.app/files/custom_assets.json"
       }
     },
     "aacs.carControl": {
       "assets": {
         "customAssetsPath": "/data/user/10/com.example.ivi.example.alexa.app/files/custom_assets.json"
       },
       ...
     }
   }
   ```

5. In your Car Control configuration (`aacs_customcarcontrol_config.json`), you can then refer to
   your custom assets by the `assetId` defined in the `custom_assets.json` file. For example, to
   define an endpoint that includes a custom asset in its list of `friendlyNames`:

   __aacs_customcarcontrol_config.json:__

   ```json
   {
     "endpointId": "default.custom_device",
     "endpointResources": {
       "friendlyNames": [
         {
           "@type": "asset",
           "value": {
             "assetId": "My.Alexa.Automotive.DeviceName.CustomDevice"
           }
         }
       ]
     }
   }
   ```

### Handle Car Control messages

Once you have configured your `CustomCarControlHandlerService`, you can implement parsing and
handling of the CarControl AASB JSON messages, as explained in
[Parsing AASB messages](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service#parsing-aasb-messages).

You will need to perform different actions on your vehicle devices, depending on the type of message
you receive:
- change the power state of an endpoint (`SetControllerValue` messages with `POWER` capability type)
- toggle the state of an endpoint property (`SetControllerValue` messages with `TOGGLE` capability
  type)
- set the mode of an endpoint property (`SetControllerValue` messages with `MODE` capability type)
- set the numeric setting of an endpoint property (`SetControllerValue` message with `RANGE`
  capability type)
- adjust (increase or decrease) the mode of an endpoint property (`AdjustControllerValue` message
  with `MODE` capability type)
- adjust (increase or decrease) the numeric setting of an endpoint property (`AdjustControllerValue`
  message with `RANGE` capability type)

Please refer to the Alexa Auto
[Using the Car Control Module AASB Messages](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control/#using-the-car-control-module-aasb-messages)
page and to the example `CustomCarControlHandlerService` for more details on how to parse and handle
these different types of messages.

### Deploy your custom Car Control handler service

Follow the steps in
[Create a service host](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service#create-a-service-host)
and
[Configure the service host deployment](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service#configure-the-service-host-deployment)
to deploy the custom Car Control handler service in your application.

## References

- [Alexa Auto Car Control Module](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/car-control)
- [Alexa Auto Car Control AASB Reference](https://alexa.github.io/alexa-auto-sdk/docs/aasb/car-control/CarControl)
- [Automotive Catalog of Assets](https://github.com/alexa/alexa-auto-sdk/blob/master/modules/car-control/assets/assets-1P.json)
- [Create a Custom Alexa Handler Service](/tomtom-indigo/documentation/tutorials-and-examples/voice-personal-assistant/create-a-custom-alexa-handler-service)
- TomTom IndiGO [AlexaHandlerService](TTIVI_INDIGO_API)
