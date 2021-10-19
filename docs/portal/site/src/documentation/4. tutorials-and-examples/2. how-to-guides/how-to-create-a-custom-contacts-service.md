---
title: How to Create a Custom Contacts Service
layout: default
---

The IVI platform comes with a [ContactsService](TTIVI_INDIGO_API) interface for getting access to 
address book contacts. A product may provide its own implementation of the 
[ContactsService](TTIVI_INDIGO_API) interface or use an existing implementation. In order for the 
UI to be able to display contacts from this service, a custom contacts service needs to be written. 
This document describes how to do this.

## How to create a custom contacts service

The following sections describe how to create a custom contacts service implementation.

### Service module setup

To create a custom contacts service, add a manifest file to your module and add a dependency to
the [ContactsService](TTIVI_INDIGO_API) to your gradle file.

Your `AndroidManifest.xml` should contain:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.tomtom.ivi.example.service.customcontacts"/>
```

Your gradle file should contain:

```kotlin
dependencies {
    implementation("com.tomtom.ivi.platform:core_serviceapis_contacts:1.0.1793")
}
```

### Service configuration

To configure a contacts service to use your custom implementation, define a service host
configuration class that inherits from the `IviServiceHostConfig` class. This class should be placed
in a file, like `ContactsServiceHostConfig.kt` in the `buildScr` module, within the`config.services` 
folder.

```kotlin
val customContactsServiceHost = IviServiceHostConfig(
    serviceHostBuilderName = "CustomContactsServiceHostBuilder",
    implementationModule = ExampleModuleReference("services_customcontacts"),
    interfaces = listOf(
        IviServiceInterfaceConfig(
            serviceName = "ContactsService",
            serviceApiModule = IviPlatformModuleReference("core_serviceapis_contacts")
        )
    )
)
```

In this configuration, the `services_customcontacts` module defines the implementation for
the `core_serviceapis_contacts` interface.

In order to create the service host configuration named `CustomContactsServiceHost`, the IVI
platform needs a service host builder class with the specific
name `CustomContactsServiceHostBuilder`.

```kotlin
class CustomContactsServiceHostBuilder : SimpleIviServiceHostBuilder() {

    override fun createIviServices(iviServiceHostContext: IviServiceHostContext) =
        listOf(CustomContactsService(iviServiceHostContext))

    companion object
}
```

__Note__
Every service host configuration needs to be registered in your application. This is necessary to
know which service should be started with which implementation when a client requires the access to
a service api.

To register this configuration, add the service host to your application gradle file:

```kotlin
ivi {
    application {
        services {
            // Register the custom contacts service.
            addHost(customContactsServiceHost)
        }
    }
}
```

### Service definition

In order to create a custom contacts service implementation you need to create a class that inherits
from `ContactsServiceBase` class.

```kotlin
class CustomContactsService(iviServiceHostContext: IviServiceHostContext) :
    ContactsServiceBase(iviServiceHostContext) {
    // A mutable list of contacts that can be updated with the contactsSource changes.
    private val mutableContacts = MutableMirrorableMap<ContactId, Contact>()
}
```

__Note__
The contacts service implementation can define any kind of source for the list of contacts, like for
example the Android's 
[Contacts provider](https://developer.android.com/guide/topics/providers/contacts-provider).

In this example the contacts service contains two contacts. The list of contacts contains those two
contacts after initialization. Only the contact with the id `1` has an image. The mutable
list `contactsSource` is the source of contacts:

```kotlin
// The source of contacts.
private val contactsSource = mutableListOf(
    Contact(
        contactId = ContactId("1"),
        displayName = "John Smith",
        initials = "JS",
        givenName = "John",
        familyName = "Smith",
        companyName = "Company Name",
        phoneNumbers = listOf(PhoneNumber("+281111111", PhoneNumberType.Main)),
        defaultPhoneNumberIndex = 0,
        addresses = listOf(Address("45 Some Street, SomeCity, SC 10000", AddressType.Home)),
        defaultAddressIndex = 0,
        favorite = true,
        primarySortKey = "John Smith",
        alternativeSortKey = "Smith John"
    ),
    Contact(
        contactId = ContactId("2"),
        displayName = "Kelly Goodwin",
        initials = "KG",
        givenName = "Kelly",
        familyName = "Goodwin",
        companyName = "Another Company Name",
        phoneNumbers = listOf(PhoneNumber("+2822222222", PhoneNumberType.Mobile)),
        defaultPhoneNumberIndex = 0,
        addresses = listOf(
            Address(
                "45 Some Other Street, SomeOtherCity, SC 12000",
                AddressType.Work
            )
        ),
        defaultAddressIndex = 0,
        favorite = false,
        primarySortKey = "Kelly Goodwin",
        alternativeSortKey = "Goodwin Kelly"
    ),
)
```

### Service lifecycle

To manage the initialization and destruction of the service, override the `onCreate` and `onDestroy`
methods.

When the service is created:

```kotlin
override fun onCreate() {
    super.onCreate()
    // Initialize the synchronization status.
    synchronizationStatus = SynchronizationStatus.NO_CONNECTED_DEVICES
    // Bind the contacts property to an empty mutable map.
    contacts = mutableContacts
    // Set the service ready and clients can call any Api of the service.
    serviceReady = true

    // The source of contacts is ready and synchronization starts.
    synchronizationStatus = SynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS
    // Updating the contacts property with some contacts from the source
    mutableContacts.putAll(contactsSource.map { it.contactId to it }.toMap())
}
```

The [ContactsService](TTIVI_INDIGO_API)`.contacts` and 
[ContactsService](TTIVI_INDIGO_API)`.synchronizationStatus` properties have been initialized.

When the service is destroyed:

```kotlin
override fun onDestroy() {
    // Put here cleaning code if necessary.
    super.onDestroy()
}
```

### Custom API implementation

To provide clients with contact images, override the [ContactsService.getImage] method with your
custom implementation. In this example only `ContactId("1")` has an image.

```kotlin
override suspend fun getImage(contactId: ContactId): Bitmap? {
    return when (contactId) {
        // Return a bitmap image for contactId 1.
        ContactId("1") -> Bitmap.createBitmap(
            IntArray(50 * 50) { Color.BLACK }, 50, 50, Bitmap.Config.ARGB_8888
        )
        // Return null for other contacts.
        else -> null
    }
}
```

## External links

- Contacts service.
- [Contacts provider](https://developer.android.com/guide/topics/providers/contacts-provider).

[TODO(IVI-3777)]: # (Add the link to contacts service module or reference API)

