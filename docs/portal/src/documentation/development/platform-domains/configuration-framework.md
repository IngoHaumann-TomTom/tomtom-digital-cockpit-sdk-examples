---
title: Configuration Framework
layout: default
---

TomTom IndiGO supports customization on many levels, from configuration of functional features
to theming of the user interface. One form of customization is achieved through the use of the
TomTom IndiGO configuration framework which can be used to specify, for example, API keys or
configuration feature flags to toggle features on or off and other values that a component in the
IVI system can use to configure itself at runtime.

A component such as an IVI service can use the configuration framework to define a configuration.
A configuration may be either static or dynamic.

Static configurations are loaded once at startup and have constant values that do not change at
runtime. This type of configuration is suitable for specifying, for example: URLs, authentication
keys, or certificates.

Dynamic configurations can change at runtime. They start off with initial values (factory defaults)
and their values can change while the system is running. For example, they can store user preferences
which the user can set to a different value while using the system. Dynamic configurations keep
hold of the full history of default values. This allows an existing setting value to be updated from
any previous version to the latest suitable value in a compatible way. See also
[Settings framework](/tomtom-indigo/documentation/development/platform-domains/settings-framework).

Every module may use static configurations directly via a static configuration provider,
or access settings via settings services. Dynamic configurations can be used by settings services
only.

## Static configuration

A static configuration is a type-safe key-value container.
The type of a value depends on the type of the key. For example,
[`BooleanStaticConfigurationKey`](TTIVI_INDIGO_API) is the key for a boolean value,
[`IntStaticConfigurationKey`](TTIVI_INDIGO_API) is the key for an integer value, etc. A static
configuration key may also address an optional (nullable) value. For example,
[`OptIntStaticConfigurationKey`](TTIVI_INDIGO_API) for a nullable integer value `Int?`.

### Static configuration providers

A static configuration provider resolves queries for static configuration values.
Clients use an instance of [`StaticConfigurationProvider`](TTIVI_INDIGO_API) from
[`FrontendContext`](TTIVI_INDIGO_API) in frontends, and
[`IviServiceHostContext`](TTIVI_INDIGO_API) in services.

Under the hood, the instance of [`StaticConfigurationProvider`](TTIVI_INDIGO_API) aggregates static
configuration providers of the [`ComposableStaticConfigurationProvider`](TTIVI_INDIGO_API) type.
They are queried in strict order until the first non-`null` value is returned.

A composable static configuration provider is responsible for loading a configuration from storage.
For example, from Android resources, Android assets, etc. It may reply a `null` value for unknown
keys. When a provider replies with a `null` value, the
[`StaticConfigurationProvider`](TTIVI_INDIGO_API) instance queries the next provider. If all
providers return a `null` value for a key to a non-`null` value, then the application crashes, and
for a key to a nullable value, the result will be `null`.

### Static configurations from Android resources

By default, IVI applications use the static configuration provider
[`ResourceStaticConfigurationProvider`](TTIVI_INDIGO_API) that reads configurations from Android
resources. Access to Android resources is well optimized, so it can be used to store critical
configurations that must be available at startup.

Static configurations are created with the configuration generator.
See the API reference documentation of
[`IviConfigurationGeneratorConfig`](TTIVI_INDIGO_GRADLEPLUGINS_API).

## Dynamic configuration

A dynamic configuration is a type-safe key-value versioned container.
The type of a value depends on the type of the key. For example,
[`BooleanDynamicConfigurationKey`](TTIVI_INDIGO_API) is the key for a boolean value,
[`IntDynamicConfigurationKey`](TTIVI_INDIGO_API) is the key for an integer value, etc.
The key consists of two parts: a configuration group and an identifier. The configuration group
gathers related configuration values. The identifier is a unique name within the configuration
group.

A dynamic configuration value [`DynamicConfigurationValue`](TTIVI_INDIGO_API) contains multiple
entries where each entry has a default value, a version when a value has been set, and an update
strategy for the existing setting value. The configuration value contains the full history of the
default values, that allows updating an existing setting value from any past version to the latest
in a controlled way.

### Dynamic configuration providers

A dynamic configuration provider resolves queries for dynamic configuration values.
Settings services use an instance of [`DynamicConfigurationProvider`](TTIVI_INDIGO_API) from
[`IviServiceHostContext`](TTIVI_INDIGO_API) to load initial values for settings.

Under the hood, the instance of [`DynamicConfigurationProvider`](TTIVI_INDIGO_API) aggregates
dynamic configuration providers of the [`ComposableDynamicConfigurationProvider`](TTIVI_INDIGO_API)
type. They are queried in strict order until the first non-`null` value is returned.

A composable dynamic configuration provider is responsible for loading a configuration from storage.
For example, from Android resources, Android assets, etc. It may reply a `null` value for unknown
keys. When a provider replies with a `null` value, the
[`DynamicConfigurationProvider`](TTIVI_INDIGO_API) instance queries the next provider. If all
providers return a `null` value, then the application crashes.

### Dynamic configurations from Android assets

By default, IVI applications use the dynamic configuration provider
[`JsonDynamicConfigurationProvider`](TTIVI_INDIGO_API) that reads configuration from JSON files
from Android assets. A configuration resource is a JSON file (or files). It is named after the
dynamic configuration group, thus all related configuration values are in the same file.
Configuration files are located in the `configurations` directory in Android assets.

Dynamic configurations are created with the configuration generator.
See the API reference documentation of
[`IviConfigurationGeneratorConfig`](TTIVI_INDIGO_GRADLEPLUGINS_API).

## Configuration providers setup

The default TomTom IndiGO application uses [`ResourceStaticConfigurationProvider`](TTIVI_INDIGO_API)
for static configurations and [`JsonDynamicConfigurationProvider`](TTIVI_INDIGO_API) for dynamic
configurations. This behavior is defined in [`IviApplication`](TTIVI_INDIGO_API) and can be changed
by overriding the `IviApplication.createStaticConfigurationProviders` and
`IviApplication.createDynamicConfigurationProviders` methods.
They return a list of static and dynamic configuration providers respectively.

