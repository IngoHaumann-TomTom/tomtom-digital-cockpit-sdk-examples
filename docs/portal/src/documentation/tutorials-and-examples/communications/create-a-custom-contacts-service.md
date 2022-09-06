---
title: Create a Custom Contacts Service
---

The IVI platform comes with a [`ContactsService`](TTIVI_INDIGO_API) interface for getting access to
address book contacts. A product may provide its own implementation of the
[`ContactsService`](TTIVI_INDIGO_API) interface or use an existing implementation. In order for the
UI to be able to display contacts from this service, a custom contacts service needs to be written.
This document describes how to do this.

## How to create a custom contacts service

The following sections describe how to create a custom contacts service implementation.

The example code for the concepts presented here is provided in `examples/telephone/customcontacts`.

### Service module setup

To create a custom contacts service, add a manifest file to your module and add a dependency to
the [`ContactsService`](TTIVI_INDIGO_API) to your Gradle file.

Your
[`AndroidManifest.xml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/AndroidManifest.xml#L14)
should contain:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.example.ivi.example.telephony.customcontacts" />
```

Your
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/build.gradle.kts#L20)
file should contain:

```kotlin
dependencies {
    implementation("com.tomtom.ivi.platform:platform_contacts_api_service_contacts")
}
```

### Service configuration

To configure a contacts service to use your custom implementation, define a service host
configuration class that inherits from the [`IviServiceHostConfig`](TTIVI_INDIGO_GRADLEPLUGINS_API)
class. This class should be placed in the application Gradle build file.

[`examples/telephony/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/app/build.gradle.kts#L24-L35)

```kotlin
val customContactsServiceHost = IviServiceHostConfig(
    serviceHostBuilderName = "CustomContactsServiceHostBuilder",
    implementationModule = ExampleModuleReference("services_customcontacts"),
    interfaces = listOf(
        IviServiceInterfaceConfig(
            serviceName = "ContactsService",
            serviceApiModule = IviPlatformModuleReference("platform_contacts_api_service_contacts")
        )
    )
)
```

In this configuration, the `services_customcontacts` module defines the implementation for
the `platform_contacts_api_service_contacts` interface.

In order to create the service host configuration named `CustomContactsServiceHost`, the IVI
platform needs a service host builder class with the specific
name `CustomContactsServiceHostBuilder`.

[`src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsServiceHostBuilder.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsServiceHostBuilder.kt#L21-L29)

```kotlin
class CustomContactsServiceHostBuilder : SimpleIviServiceHostBuilder() {

    override fun createIviServices(
        iviServiceHostContext: IviServiceHostContext
    ): Collection<AnyIviServiceBase> =
        listOf(CustomContactsService(iviServiceHostContext))

    companion object
}
```

__Note:__ Every service host configuration needs to be registered in your application. This is
necessary to know which service should be started with which implementation when a client requires
the access to a service api.

To register this configuration, add the service host to your application
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/app/build.gradle.kts#L57-L59)
file:

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
from the `ContactsServiceBase` class.

[`src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt#L31-L35)

```kotlin
internal class CustomContactsService(iviServiceHostContext: IviServiceHostContext) :
    ContactsServiceBase(iviServiceHostContext) {
    // A mutable list of contacts that can be updated with the contactsSource changes.
    private val mutableContacts = MutableMirrorableMap<ContactId, Contact>()
}
```

__Note:__ The contacts service implementation can define any kind of source for the list of
contacts, for example the Android
[Contacts provider](https://developer.android.com/guide/topics/providers/contacts-provider).

In this example the contacts service contains two contacts. The list of contacts contains those two
contacts after initialization. Only the contact with the id `1` has an image. The mutable
list `contactsSource` is the source of contacts:

[`src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt#L41-L77)

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

[`src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt#L79-L97)

```kotlin
override fun onCreate() {
    super.onCreate()
    // Initialize the synchronization status.
    phoneBookSynchronizationStatus = PhoneBookSynchronizationStatus.NO_CONNECTED_DEVICES
    // Bind the contacts property to an empty mutable map.
    contacts = mutableContacts
    // Set the service ready and clients can call any Api of the service.
    serviceReady = true

    // The source of contacts is ready and synchronization starts.
    phoneBookSynchronizationStatus = PhoneBookSynchronizationStatus.SYNCHRONIZATION_IN_PROGRESS
    // Updating the contacts property with some contacts from the source
    mutableContacts.putAll(contactsSource.map { it.contactId to it }.toMap())
}
```

The [`ContactsService`](TTIVI_INDIGO_API)`.contacts` and
[`ContactsService`](TTIVI_INDIGO_API)`.phoneBookSynchronizationStatus` properties have been
initialized.

When the service is destroyed:

[`src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt#L99-L102)

```kotlin
override fun onDestroy() {
    // Put here cleaning code if necessary.
    super.onDestroy()
}
```

### Custom API implementation

To provide clients with contact images, override the
[`ContactsService`](TTIVI_INDIGO_API)'s `getImage()` method with your custom implementation. In
this example only `ContactId("1")` has an image.

[`src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customcontacts/src/main/kotlin/com/example/ivi/example/telephony/customcontacts/CustomContactsService.kt#L104-L113)

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

- TomTom IndiGO [`ContactsService`](TTIVI_INDIGO_API).
- Android [`Contacts provider`](https://developer.android.com/guide/topics/providers/contacts-provider).


