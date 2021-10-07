---
title: How to Use the Configuration Framework
layout: default
---

IndiGO supports customization at many levels, from functional features to theming.
Customization is achieved through configurations that contain feature flags and other values.
A configuration may be either static or dynamic.

Static configurations have constant values that do not change at runtime and are loaded once at
startup. For example, URLs, authentication keys, or certificates.

Dynamic configurations contain the initial values (factory defaults) for settings that can be
modified at runtime. For example, user preferences. Dynamic configurations contain the full history
of the default values, that allows updating an existing setting value from any past version to the
latest in a controlled way.

Every module may use static configurations directly via a static configuration provider,
or access settings via settings services. Dynamic configurations can be used by settings services
only.

This document describes how to create static and dynamic configurations using the configuration
generator and how to use these configurations in the application.

## Overview of the example application

The example application adds the account frontend for the default IndiGO application. The account
frontend adds new panels to show account information or login page, which can be invoked by a menu
item. Account status is managed by the account service. When the user is authenticated, the user
login is stored in persistent storage by the account settings service, so the user does not need to
log in again after restart of the application.
The source code for the frontend and service can be found at [modules](/modules).
Also see [how to create IVI service](how-to-create-an-ivi-service.html), and the API reference
documentation for an `IviService` annotation.

To demonstrate the usage of configurations, the account service will use a static configuration
to get the URL of the online API endpoint to authenticate a user.
The login will be stored for a limited time. The account settings service will provide the time, as
specified by a dynamic configuration.
For that, you need to perform the following steps:

1. [Create configuration files](#create-configuration-files)
2. [Enable the configuration generator](#enable-the-configuration-generator)
3. [Access the configurations via configuration providers](#access-the-configurations-via-configuration-providers)

## Create configuration files

Static and dynamic configurations are defined in JSON files located in the `configurations/static`
and `configurations/dynamic` directories of the source set.
For example: `<moduleRoot>/src/main/configurations/static`.

The JSON file schema is fully documented in the API reference documentation of
`com.tomtom.ivi.platform.gradle.api.common.configurationgenerator.IviConfigurationGeneratorConfig`.

### Static configuration file

To create a static configuration for the account service, create a JSON file
`modules/services/account/src/main/configurations/static/com.tomtom.ivi.example.account.json`.

```json
{
  "packageName": "com.tomtom.ivi.example.account",
  "keys": [
    {
      "name": "onlineAccountEndpointConfigKey",
      "description": "The URI of the endpoint of the online account API",
      "type": "String",
      "value": "https://www.example.com/account/"
    }
  ]
}
```

This file defines a __string__ configuration with an `"onlineAccountEndpointConfigKey"` key that
belongs to the package `com.tomtom.ivi.example.account`.
The configuration contains the URL of the online API endpoint, as specified by the `"value"` field.

__Note:__ The key name must have a `ConfigKey` suffix, and be suitable to use as
a [Kotlin property name](https://kotlinlang.org/docs/coding-conventions.html#property-names).

__Note:__ The `"description"` is an optional field. It is used as KDoc for the generated
configuration keys.

### Dynamic configuration file

To create a dynamic configuration for the account settings service, create a JSON file
`modules/services/accountsettings/src/main/configurations/dynamic/com.tomtom.ivi.example.account.json`.

```json
{
  "version": 2,
  "packageName": "com.tomtom.ivi.example.account",
  "keys": [
    {
      "name": "onlineLoginValidPeriodInDaysConfigKey",
      "description": "Number of days that an online login stays valid.",
      "type": "Long",
      "values": [
        {
          "value": 1,
          "fromVersion": 1,
          "updateStrategy": "always"
        },
        {
          "value": 30,
          "fromVersion": 2,
          "updateStrategy": "always"
        }
      ]
    }
  ]
}
```

This file defines a __long number__ configuration with an
`"onlineLoginValidPeriodInDaysConfigKey"` key that belongs to the package
`com.tomtom.ivi.example.account`.

__Note:__ The key name must have a `ConfigKey` suffix, and be suitable to use as
a [Kotlin property name](https://kotlinlang.org/docs/coding-conventions.html#property-names).

The `"version"` field specifies the revision number of the configuration file that shall be
increased with any change to the configuration. The configuration keeps values for previous
versions. They are used for updating old values in the persistent storage on the device to the
current version.
The configuration version is `2`, so the current value is `30`. The `"updateStrategy"` field defines
how old values are updated. In the example, it defines that old values are overridden with the new
values. See the API reference documentation for `ConfigurationUpdateStrategy`.
The latest configuration version will be stored on the device, and will remain unchanged until the
configuration file with newer version is loaded.

__Note:__ The `"description"` is an optional field. It is used as KDoc for the generated
configuration keys.

### Enable the configuration generator

The configuration JSON files are suitable for human editing but not yet ready to be used in the
application. To make the configurations available in the application, they need to be processed by
the configuration generator.

The configuration generator looks for JSON files and processes them into:

- Kotlin source files with configuration keys that can be used by the application. Generated keys
  are wrapped into `StaticConfiguration` or `DynamicConfiguration` objects.
- Resource files with static configuration values.
- Android asset files with dynamic configuration values.

To enable the configuration generator, add following Gradle configuration into Gradle build
configurations of modules that contains the configuration.
For the given example, these are the account service module
(__modules/services/account/build.gradle.kts__),
and the account settings service module
(__modules/services/accountsettings/build.gradle.kts__).

```kotlin
ivi {
    configurationGenerator {
        enabled = true
    }
    // The configuration framework is an experimental feature, and has to be explicitly opted in.
    optInToExperimentalApis = true
}
```

Then run Gradle task `generateConfigurations`:

```bash
# Either for all projects.
./gradlew generateConfigurations

# Or for services projects only.
./gradlew services_accountsettings:generateConfigurations services_account:generateConfigurations
```

__Note__: The task is a dependency for `preDebugBuild` and `preReleaseBuild` tasks, so it is
executed automatically during `assemble` or `build` tasks.

### Access the configurations via configuration providers

#### Static configuration value

Static configurations are provided by the static configuration provider that is in the context of
IVI service `iviServiceHostContext.staticConfigurationProvider`.

To get the configuration value in the `StockAccountService`, use the
`com.tomtom.ivi.example.account.StaticConfiguration.onlineAccountEndpointConfigKey` variable.

__modules/services/account/src/main/kotlin/com/tomtom/ivi/example/service/account/StockAccountService.kt__

```kotlin
import com.tomtom.ivi.example.account.StaticConfiguration.onlineAccountEndpointConfigKey

class StockAccountService(iviServiceHostContext: IviServiceHostContext) :
    AccountServiceBase(iviServiceHostContext) {
    
    private val onlineAccountEndpoint =
        iviServiceHostContext.staticConfigurationProvider[onlineAccountEndpointConfigKey]
}    
```

The `onlineAccountEndpoint` is a string with the configuration value that is used during user log
in. The `logIn` method calls `logInOnline()` to authenticate the user. The latter does not make a
real network query but does simple validation only.

__modules/services/account/src/main/kotlin/com/tomtom/ivi/example/service/account/StockAccountService.kt__

```kotlin
private fun logInOnline(username: String, password: SensitiveString): Account? =
    takeIf { isValidUsername(username) && isValidPassword(password.value) }?.run {
        println("Pretend online request to $onlineAccountEndpoint.")
        Account(username)
    }
```

#### Dynamic configuration value

Dynamic configurations are used by settings services to initialize settings.

First, add new setting `onlineLoginValidPeriodInDays` to the account settings service that holds the
amount of days the user may stay logged in.

__modules/serviceapis/accountsettings/src/main/kotlin/com/tomtom/ivi/example/serviceapi/accountsettings/AccountSettingsService.kts__

```kotlin
interface AccountSettingsService {
    @IviSetting(accessMode = IviSettingAccessMode.READ_ONLY)
    val onlineLoginValidPeriodInDays: Long
}
```

By default, the settings service initializes properties with values from dynamic configurations.
The service implementation only needs to set the configuration key and the setting key.
The latter can be easily made from the former.

__modules/services/accountsettings/src/main/kotlin/com/tomtom/ivi/example/service/accountsettings/StockAccountSettingsService.kt__

```kotlin
// Both variables are generated by the configuration generator.
import com.tomtom.ivi.example.account.DynamicConfiguration.settingKeyPrefix
import com.tomtom.ivi.example.account.DynamicConfiguration.onlineLoginValidPeriodInDaysConfigKey

class StockAccountSettingsService(iviServiceHostContext: IviServiceHostContext) :
    AccountSettingsServiceBase(iviServiceHostContext) {
    
    // The `onlineLoginValidPeriodInDays` setting has default values provided by the dynamic
    // configuration key.
    // All methods for this setting have default implementation.
    override val onlineLoginValidPeriodInDaysConfigurationKey: LongDynamicConfigurationKey =
        onlineLoginValidPeriodInDaysConfigKey
        
    // This key is used to load and store the setting value in the persistent storage on the device.
    override val onlineLoginValidPeriodInDaysSettingKey: LongSettingKey =
        onlineLoginValidPeriodInDaysConfigurationKey.toSettingKey(
            // The setting is application-wide, regardless of the currently selected user profile.
            SettingScope.APPLICATION,
            settingKeyPrefix
        )
}
```

Then the account service can use the `onlineLoginValidPeriodInDays` settings to validate the
logged in account when it is needed.
