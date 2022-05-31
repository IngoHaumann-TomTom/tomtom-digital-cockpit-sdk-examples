---
title: Integrating Amazon Alexa
---

TomTom IndiGO can support the use of multiple voice personal assistants (VPAs) in a single product.
The end-user can invoke each one with a separate wake-up-word and then use them one at a time.
TomTom IndiGO comes with a pre-integration of Amazon's VPA, Alexa, specifically
[Alexa Auto](https://github.com/alexa/alexa-auto-sdk).

To be able to use Alexa in your product, you will need to integrate some additional Android APKs in
your development platform and configure some development account information into your application.
This page explains how to do this.

## Integrating Amazon Alexa on Hardware

You can skip this section if you are trying to run Amazon Alexa on the TomTom IndiGO emulator or on
the TomTom IndiGO reference hardware (Samsung Galaxy S5e WiFi tablet).

Follow these steps to integrate Amazon Alexa on your custom hardware platform:

1. Download the Alexa APKs:
   - [lve-app-4.0.0.apk](https://repo.tomtom.com/repository/ivi/com/amazon/alexa/aace/lve-app/4.0.0/lve-app-4.0.0.apk)
   - [lm-app-en_US-4.0.0.apk](https://repo.tomtom.com/repository/ivi/com/amazon/alexa/aace/lm-app-en_US/4.0.0/lm-app-en_US-4.0.0.apk)
1. Sign the Alexa APKs with the same key that was used for signing the TomTom IndiGO APK
1. Install the signed Alexa APKs on your device:
   ```cmd
   adb install lve-app-4.0.0-signed.apk
   adb install lm-app-en_US-4.0.0-signed.apk
   ```
1. Set the SELinux policy on your device to be permissive:
   ```cmd
   adb shell setenforce permissive
   ```
   Alternatively, add the line below to one of your SELinux policy files:
   ```cmd
   # Allow platform_app to manage UDS sockets. This is needed both
   # for Navkit2 and Alexa Local Voice Controller.
   allow platform_app app_data_file:sock_file create_file_perms;
   ```

## Integrating Amazon Alexa in your product

To be able to use Alexa in your TomTom IndiGO-based product, there are some steps that need to be
followed:
1. [Apply the Alexa plugins](#apply-the-alexa-plugins)
1. [Register your Alexa product with Amazon](#register-your-alexa-product-with-amazon)
1. [Specify your Amazon Alexa IDs](#specify-your-amazon-alexa-ids)

### Apply the Alexa plugins

Declare a reference to the Alexa plugin library in the `/build-logic/libraries.versions.toml` file:
```toml
gradlePluginApiAppsuiteDefaultsAlexa = { module = "com.tomtom.ivi.appsuite.gradle.alexa:api_appsuitedefaults_alexa", version.ref = "indigoPlatform" }
```

Add a dependency on the Alexa plugin library in the `/buildSrc/build.gradle.kts` file:
```kotlin
dependencies {
    ...
    implementation(libraries.gradlePluginApiAppsuiteDefaultsAlexa)
    ...
}
```

Apply the Alexa plugins in the product's `build.gradle.kts` file:
```kotlin
plugins {
    ...
    id("com.tomtom.ivi.appsuite.alexa.defaults.alexa")
    id("com.tomtom.ivi.appsuite.alexa.defaults.config")
}
```
These plugins will add the Alexa TomTom IndiGO application to your product and will provide a simple
way to configure the Alexa application.

### Register your Alexa product with Amazon

In order to use Amazon Alexa as part of your TomTom IndiGO-based product, some information is needed
from Amazon's developer site.

A list of the prerequisites is provided below:

- An [Amazon Developer Account](https://developer.amazon.com/). If you are reading this document,
  then you or your company will already have communication or a business relationship with Amazon,
  who will have made Alexa Auto dependencies and non-public documentation available to you.
  Your Amazon contact can assist you with any issues relating to Amazon accounts. TomTom has no
  control over your Amazon Developer Account.
- Within your Amazon Developer Account, an Alexa Voice Service product and security profile should
  have been set up. See
  [Amazon's instructions](https://alexa.github.io/alexa-auto-sdk/docs/get-started/#register-an-avs-product)
  or speak to your Amazon contact to understand how to do this.
- You will need your product's _Product ID_ and _Client ID_ which can be obtained from the
  [Amazon Voice Service console](https://developer.amazon.com/alexa/console/avs/products)
  which you can access from within your Amazon Developer Account.

### Specify your Amazon Alexa IDs

Now that you have your _Product ID_ and _Client ID_ ready, you can configure the product so that
they can be used with the TomTom IndiGO SDK.

The Amazon Alexa IDs can be configured in various ways depending on your development and
production needs:
- [Via `local.properties` or Gradle properties](#via-local.properties-or-gradle-properties).
- [By overriding the static configuration values in Android resources](#by-overriding-the-static-configuration-values-in-android-resources).
- [With a static configuration provider](#with-a-static-configuration-provider).

#### Via local.properties or Gradle properties

The _Product ID_ and _Client ID_ are specified by the properties named `alexaDeviceInfoProductId`
and `alexaDeviceInfoClientId` respectively.

The easiest way is to set the two properties via the top-level `local.properties` file or
as Gradle properties.

This option is preferred if you do not want to add the ID to your SCM (such as Git). In this
case you can configure the IDs in the `local.properties` file (which is not supposed to be added
to an SCM) or configure the key in the `gradle.properties` file in the Gradle user home directory.
The latter option makes the IDs available in any Gradle project.

#### By overriding the static configuration values in Android resources

You can override static configuration values in Android resources.

For example:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<resources>
    <!--The client ID used to authorize the client with AVS.-->
    <string name="alexaDeviceInfoClientIdConfigKey">my-client-id-from-Amazon-developer-site</string>
    <!--The product ID used to authorize the client with AVS.-->
    <string name="alexaDeviceInfoProductIdConfigKey">MyProductId</string>
</resources>
```

Disable the build-time check by setting the `disableAlexaDeviceIdBuildTimeCheck` property to `true`
in the top-level `gradle.properties` file:

```
disableAlexaDeviceIdBuildTimeCheck=true
```

or by not applying the `com.tomtom.ivi.appsuite.alexa.defaults.config` Gradle plugin.

This option is preferred if you want to simplify the project setup. However, it requires the IDs to
be added to your SCM.

#### With a static configuration provider

Last but not least is to configure a static configuration provider in the Android Application class
that provides the key. For this you can use an
[`AlexaDeviceInfoStaticConfigurationProvider`](TTIVI_INDIGO_API)
instance. The `ExampleApplication` class contains an example explaining how to configure it.

Disable the build-time check by setting the `disableAlexaDeviceIdBuildTimeCheck` property to `true`
in the top-level `gradle.properties` file:

```
disableAlexaDeviceIdBuildTimeCheck=true
```

or by not applying the `com.tomtom.ivi.appsuite.alexa.defaults.config` Gradle plugin.

This option is preferred if you want to obtain and configure the IDs at runtime.

## Configuring Alexa

### Static configuration

Setting up your Amazon Alexa IDs allows your product to access Alexa so that your end-users can use
the voice assistant. There are additional parameters, however, which can be set to give Alexa more
information about the product and vehicle in which it is running.
These additional parameters can be configured
[by overriding the static configuration values in Android resources](#by-overriding-the-static-configuration-values-in-android-resources)
or
[using a static configuration provider](#with-a-static-configuration-provider).

The additional parameters correspond to the configuration values which Amazon have documented in
their
[VehicleConfiguration reference page](https://alexa.github.io/alexa-auto-sdk/docs/explore/features/core/?h=vehicleconfiguration#required-vehicle-info-configuration)
and are also listed in the table below:

| Configuration key name                      | Description                                                     |
| ------------------------------------------- | --------------------------------------------------------------- |
| `alexaDeviceInfoClientIdConfigKey`          | The client ID used to authorize the client with AVS. See [Specify your Amazon Alexa IDs](#specify-your-amazon-alexa-ids).           |
| `alexaDeviceInfoProductIdConfigKey`         | The product ID used to authorize the client with AVS. See [Specify your Amazon Alexa IDs](#specify-your-amazon-alexa-ids).           |
| `alexaDeviceInfoDeviceSerialNumberConfigKey`| The device serial number used to authorize the client with AVS. |
| `alexaDeviceInfoManufacturerNameConfigKey`  | The manufacturer name of the product.                           |
| `alexaDeviceInfoDescriptionConfigKey`       | The description of the product.                                 |
| `alexaVehicleInfoMakeConfigKey`             | The make of the vehicle.                                        |
| `alexaVehicleInfoModelConfigKey`            | The model of the vehicle.                                       |
| `alexaVehicleInfoYearConfigKey`             | The model year of the vehicle. A value of this property type must be in the range 1900-2100.                                 |
| `alexaVehicleInfoTrimConfigKey`             | The trim level of the vehicle, identifying the vehicle's level of equipment or special features. Example values: "Standard", "Sport", "Limited". |
| `alexaVehicleInfoGeographyConfigKey`        | The current location (country/region/state/etc.) of the vehicle. Example values: "US", "US-North", "WA". |
| `alexaVehicleInfoVersionConfigKey`          | The client software version. Example value: "2.2.1X". |
| `alexaVehicleInfoLanguageConfigKey`         | The language or locale selected for Alexa by the vehicle owner. Example values: "en-US", "fr-CA"." |
| `alexaVehicleInfoMicrophoneConfigKey`       | The type and arrangement of microphone used by the vehicle. Example value: "7 mic array, centrally mounted". |
| `alexaVehicleInfoCountriesConfigKey`        | A comma-delimited list of ISO codes of countries where the vehicle is operating. |
| `alexaVehicleInfoVehicleIdentifierConfigKey`| The manufacturer's identifier for the vehicle. |
| `alexaVehicleOperatingCountryConfigKey`     | The 2-letter ISO code of the main operating country of the vehicle. |
| `alexaAplEnabledConfigKey`                  | Indicates whether support for the Alexa Presentation Language is enabled. Set to false by default. |

### Dynamic configuration

The [`VpaMediatorSettingsService`](TTIVI_INDIGO_API) defines a set of VPA-related settings that the
service clients are able to change at runtime.
The default values of these settings for the Alexa VPA are defined by a
[`DynamicConfigurationProvider`](TTIVI_INDIGO_API), but, if necessary, they can be overriden by
providing a custom dynamic configuration. More information on dynamic configurations is available in
the
[configuration framework](/tomtom-indigo/documentation/tutorials-and-examples/customization/use-the-configuration-framework)
page.
The dynamic configuration values are listed in the table below:

| Configuration key name          --             | Description | Type  | Default value |
| -----------------------------------------------| ------------|------ |---------------|
| `alexaLocaleConfigKey`                         | The default value of the Alexa [`VpaSettings`](TTIVI_INDIGO_API)`.activeLocale` setting. Valid values: "en-US", "es-US", "de-DE", "en-AU", "en-CA", "en-GB", "en-IN", "es-ES", "es-MX", "fr-CA", "fr-FR", "hi-IN", "it-IT", "ja-JP", "pt-BR". | String | "en-US" |
| `alexaWakeUpWordEnabledConfigKey`              | The default value of the Alexa [`VpaSettings`](TTIVI_INDIGO_API)`.wakeUpWordEnabled` setting. | Boolean | `true` |
| `alexaSyncNavigationFavoritesEnabledConfigKey` | The default value of the Alexa [`VpaSettings`](TTIVI_INDIGO_API)`.syncVehicleNavigationFavoritesEnabled` setting. | Boolean | `true` |
| `alexaLocationSharingEnabledConfigKey`         | The default value of the Alexa [`VpaSettings`](TTIVI_INDIGO_API)`.locationSharingEnabled` setting. | Boolean | `false` |
| `alexaDoNotDisturbModeConfigKey`               | The default value of the Alexa [`VpaSettings`](TTIVI_INDIGO_API)`.doNotDisturbModeEnabled` setting. | Boolean | `false` |
| `alexaEnabledEarconsConfigKey`                 | The default value of the Alexa [`VpaSettings`](TTIVI_INDIGO_API)`.enabledEarcons` setting. Provided as a JSON array of the enabled [`VpaEarconType`](TTIVI_INDIGO_API) earcons. | JSON String | "[\\"START_LISTENING\\",\\"END_LISTENING\\"]" |
