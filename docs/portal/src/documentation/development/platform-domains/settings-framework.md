---
title: Settings Framework
layout: default
---

The settings framework allows cross-application settings to be shared between TomTom IndiGO
services and frontends in TomTom IndiGO. TomTom IndiGO components can store them persistently and
change their values.

Modules access settings via setting services that offer type-safe and domain-specific interfaces to
the generic settings management service.

## Overview

The settings framework consists of a [settings management service](#settings-management-service),
that provides a [`SettingsManagementService`](TTIVI_INDIGO_API) API to store setting values in the
persistent key-value storage. It provides [settings services](#settings-service) that offer a
type-safe and domain-specific interface to a settings management service.

The
[_off-the-shelf_, or _stock_](/tomtom-indigo/documentation/development/introduction#off-the-shelf-components-or-stock-components)
IVI frontends and IVI services use settings services to manage their settings.

![Settings framework high-level overview image](images/settings_framework-high-level-overview.svg)

## Settings management service

The settings management service [`SettingsManagementService`](TTIVI_INDIGO_API) provides an API to
store settings values in persistent key-value storage in a type-safe manner.

A client of a settings management service should _create_ a setting before use. A client specifies:

- An identifier `IviServiceId` of the service that owns the setting. Only the owning service can
  create the same setting multiple times, which may happen if the owning service has been restarted.
- A unique setting key.
- A default setting value.
- An update strategy `SettingUpdateStrategy` that specifies what happens to the existing setting
  value.

The type of a setting key is a subtype of the [`SettingKey`](TTIVI_INDIGO_API), and declares the type
of a setting value. Available setting key subtypes are:

- [`BooleanSettingKey`](TTIVI_INDIGO_API) for a __boolean__ setting value.
- [`IntSettingKey`](TTIVI_INDIGO_API) for an __int__ setting value.
- [`FloatSettingKey`](TTIVI_INDIGO_API) for a __float__ setting value.
- [`LongSettingKey`](TTIVI_INDIGO_API) for a __long__ setting value.
- [`DoubleSettingKey`](TTIVI_INDIGO_API) for a __double__ setting value.
- [`StringSettingKey`](TTIVI_INDIGO_API) for a __string__ setting value.
- [`BooleanListSettingKey`](TTIVI_INDIGO_API) for a __list of boolean__ setting values.
- [`IntListSettingKey`](TTIVI_INDIGO_API) for a __list of int__ setting values.
- [`FloatListSettingKey`](TTIVI_INDIGO_API) for a __list of float__ setting values.
- [`LongListSettingKey`](TTIVI_INDIGO_API) for a __list of long__ setting values.
- [`DoubleListSettingKey`](TTIVI_INDIGO_API) for a __list of double__ setting values.
- [`StringListSettingKey`](TTIVI_INDIGO_API) for a __list of string__ setting values.
- [`EnumSettingKey`](TTIVI_INDIGO_API) for an __enum__ setting value.

A setting key consists of a setting scope [`SettingScope`](TTIVI_INDIGO_API) and a setting identifier.
The setting scope defines whether a setting has a value per user profile, or is relevant to the
entire application, regardless of the active user profile.

__Note:__ If the settings management service restarts, then all settings must be created again by
the settings service that owns them. The restart may be detected by observing the availability of
the settings management service.

A client may get the current value of the created setting, or update the value, or reset the setting
to the default value.

A settings management service publishes a _session token_ that identifies the state of settings
visible to clients. It changes when the settings have been modified by a settings management
service, due to user profile switching, factory reset, or other reasons that are not caused by
settings services. Clients must observe the session token and use it to modify settings.

See the API reference documentation for the [`SettingsManagementService`](TTIVI_INDIGO_API) interface.

## Settings service

A settings service offers a type-safe and domain-specific interface to a settings management
service.

A settings service is a special case of a IVI service that only has properties. Properties can be
either _read-only_ or _writable_. See the [`IviSetting`](TTIVI_INDIGO_API) annotation for details.

Properties of a settings service may be of any type supported by the IVI service framework, and are
not mandatorily bound to settings. Thus a property may have a value merged from a few settings or
several properties may use the same setting, or parts of it.

During initialization, a settings service creates all required settings, and reads stored setting
values. If there is no stored value for a setting, then the default value of a setting is used.

A setting schema has a version that is defined by a settings service, and is stored persistently. A
settings service reads the stored version before creating the setting. If the stored version differs
from the current version, that is defined by the settings service, then the settings service must
update the stored settings to the current scheme.

![Initialization of a settings service](images/settings_framework-sequence-diagram-initialization-of-service.svg)

A settings service gets the default values and update strategies, required to create or update
settings, from the dynamic configuration provider `dynamicConfigurationProvider` of the
[`IviSettingsServiceBase`](TTIVI_INDIGO_API) class. See also the
[Configuration framework](/tomtom-indigo/documentation/development/platform-domains/configuration-framework).

__Note:__ A settings service may also use the static configuration provider
`staticConfigurationProvider`.

### Settings service declaration

Like [any IVI service](/tomtom-indigo/documentation/development/ivi-services), a settings service
implements an IVI service interface; see [`IviService`](TTIVI_INDIGO_API). The difference is that
the interface only contains properties annotated with the [`@IviSetting`](TTIVI_INDIGO_API)
annotation. Clients may access the service using the service API instance, created with
`<interface>.createApi()`.

### Settings service implementation

The `<Interface>Base` class, generated from the settings service interface, inherits the
[`IviSettingsServiceBase`](TTIVI_INDIGO_API) class, and contains methods to manage settings that may
be overridden for specific cases.

For settings of types supported by the settings management API, that is, which can be passed to the
API without transformation, the methods have a default-generating implementation. For such a
setting, the implementation only has to define the configuration key and the setting key. These keys
are used to load default values and update information from dynamic configurations, and to perform
initialization of a setting.

Settings of other types require the implementation of a few methods.

The base class [`IviSettingsServiceBase`](TTIVI_INDIGO_API) calls the `init<Property>()` method,
giving the version of a setting stored in persistent storage. If versions mismatch, the
implementation of the method must manage the update from the given version to the latest, specified
with the `IviSettingsServiceBase.settingsVersion` property.

The default implementation creates a setting, applying value entries of the dynamic configuration
value consecutively. The value of the current version is written to persistent storage when all
settings are initialized.

__Note:__ By default, a setting service waits for a settings management service to become
available to initialize settings. That behavior can be changed by overriding the
`IviSettingsServiceBase.initSettingsService()` method.

#### Example

The declaration of a setting service interface.

```kotlin
@IviService(
    serviceId = "org.example.services.foo"
)
interface FooSettingsService {
    /**
     * The read-only setting of the type supported by the settings management API.
     */
    @IviSetting(accessMode = IviSettingAccessMode.READ_ONLY)
    val foo: Int

    /**
     * The writable setting of the type not supported by the settings management API.
     */
    @IviSetting(accessMode = IviSettingAccessMode.READ_WRITE)
    val bar: SomeParcelableClass
 }
```

From the given interface, the IVI service framework generates an API class for service clients, and
a base class for a service implementation.

```kotlin
// Generated code.
class FooSettingsServiceApi {
    val serviceAvailable: LiveData<Boolean>

    val foo: LiveData<Int>
    val bar: LiveData<SomeParcelableClass>

    fun updateBarAsync(newValue: SomeParcelableClass, onResult: ...)
    suspend fun coUpdateBar(newValue: SomeParcelableClass)
}
```

```kotlin
// Generated code.
open class FooSettingsServiceBase {
    // Properties for keys are generated because the type of [foo] property is supported by
    // the settings management API.
    protected abstract val fooConfigurationKey: IntDynamicConfigurationKey?
    protected abstract val fooSettingKey: IntSettingKey?

    /**
     * Initializes the [foo] property.
     */
    protected open fun initFoo(storedSettingsVersion: Int) {
        // Implementation is generated by the framework because the type of [foo] property is
        // supported by the settings management API.
    }

    /**
     * Reads the value of the [foo] property from the persistent storage.
     */
    protected open suspend fun readFooFromStorage(): Int {
        // Implementation is generated by the framework because the type of [foo] property is
        // supported by the settings management API.
    }

    /**
     * Writes the value of the [foo] property to the persistent storage.
     */
    protected open suspend fun writeFooToStorage(newValue: Int) {
        // Implementation is generated by the framework because the type of [foo] property is
        // supported by the settings management API.
    }

    /**
     * Initializes the [bar] property.
     */
     // It is an abstract method because the type of the property is not supported by the settings
     // management API.
    protected abstract fun initBar(storedSettingsVersion: Int)

    /**
     * Reads the value of the [bar] property from the persistent storage.
     */
     // It is an abstract method because the type of the property is not supported by the settings
     // management API.
    protected abstract suspend fun readBarFromStorage(): SomeParcelableClass

    /**
     * Writes the value of the [bar] property to the persistent storage.
     */
     // It is an abstract method because the type of the property is not supported by the settings
     // management API.
    protected abstract suspend fun writeBarToStorage(newValue: SomeParcelableClass)

    /**
     * Updates the value of the [bar] property.
     */
    override suspend fun updateBar(newValue: SomeParcelableClass) {
        // The implementation is generated for writable properties of any type.
    }
}
```

### Settings service deployment configuration

A settings service is deployed as a regular IVI service.
It is worth adding a dependency to a settings service interface for a deployment configuration of
an IVI service host that uses the settings service.
To reduce IPC load, a settings service and the IVI service that uses it can be deployed together in
the same process.

See the documentation for
[IVI service deployment](/tomtom-indigo/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system)
for details.
