---
title: Create a Custom Connection Service
---

The TomTom IndiGO platform allows end-users to make phone calls using a Bluetooth-connected phone
using the Bluetooth Hands Free Profile. TomTom IndiGO can be extended to support other
communications services such as VOIP (Voice Over Internet Protocol) calling or integrate an SDK of
an online conference calling or messaging service and make and receive calls using the existing
communications framework for these new services.

A [`ConnectionService`](https://developer.android.com/reference/android/telecom/ConnectionService)
is a service used by Android to implement calling functionality, like phone calling or any VOIP
calling service like WhatsApp. Another communications service can be supported by implementing a
custom
[`ConnectionService`](https://developer.android.com/reference/android/telecom/ConnectionService).

## How to create a custom connection service

The following sections describe how to create a custom connection service implementation.

The example code for the concepts presented here is provided in
`examples/telephone/customconnection`.

### Service module setup

To create a custom connection service, add an `AndroidManifest.xml` file to your module. In this
file your custom service has to be declared so it can be seen by the system. This XML file should
also contain the package name together with some permissions:

Add a
[`src/main/AndroidManifest.xml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/AndroidManifest.xml#L14-L33)
file:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
package="com.example.ivi.example.telephony.customconnection">

<!-- This is needed to place and receive calls using Android's TelecomManager. -->
<uses-permission android:name="android.permission.MANAGE_OWN_CALLS" />
<!-- This is needed to read available phone accounts using Android's TelecomManager. -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />

<application>
    <!-- Declaration of the custom connection service which can be bound with the TelecomManager. -->
    <service
        android:name=".CustomConnectionService"
        android:permission="android.permission.BIND_TELECOM_CONNECTION_SERVICE">
        <intent-filter>
            <action android:name="android.telecom.ConnectionService" />
        </intent-filter>
    </service>
</application>
</manifest>
```

Add a dependency to the [`TelecomService`](TTIVI_PLATFORM_API) and
[`LifecycleService`](https://developer.android.com/reference/android/arch/lifecycle/LifecycleService)
in your
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/build.gradle.kts#L21)
file:

```kotlin
dependencies {
    implementation(libraries.iviPlatformTelecomApiServiceTelecom)
    implementation("androidx.lifecycle:lifecycle-service:2.3.1")
}
```

The dependency to the [`TelecomService`](TTIVI_PLATFORM_API) is necessary in order to use
[`CallState`](TTIVI_PLATFORM_API) and
`toPhoneUri` (package [com.tomtom.ivi.platform.telecom.api.common.utils](TTIVI_PLATFORM_API)),
both required for the phone call state management.

The dependency to the
[`LifecycleService`](https://developer.android.com/reference/android/arch/lifecycle/LifecycleService)
is necessary in order to create a dispatcher in your custom service class, as it is not possible to
inherit from
[`LifecycleService`](https://developer.android.com/reference/android/arch/lifecycle/LifecycleService)
directly:

[`src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt#L48)

```kotlin
private val dispatcher = ServiceLifecycleDispatcher(this)
```

### Service preparation

To use your custom implementation you need to create an internal object
`CustomConnectionServiceHolder.kt` that will create and store an instance of
`CustomConnectionService`.

[`src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionServiceHolder.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionServiceHolder.kt#L21-L42)

```kotlin
internal object CustomConnectionServiceHolder {
    // The custom connection service used for communications.
    @Volatile
    private var customConnectionService: CustomConnectionService? = null

    // It should be called when we create the CustomConnectionService, so the instance of the
    // CustomConnectionService is initialized.
    @Synchronized
    fun setCustomConnectionService(customConnectionService: CustomConnectionService?) {
        this.customConnectionService = customConnectionService
    }

    // Checks if the connection service is ready.
    @Synchronized
    fun isCustomConnectionServiceReady() = (customConnectionService != null)

    //...
}
```

`CustomConnectionService` will be set when the
[`TelecomManager`](https://developer.android.com/reference/android/telecom/TelecomManager) binds to
the `CustomConnectionService`, which will happen when creating an incoming or outgoing call. If
there is no more ongoing call, the
[`TelecomManager`](https://developer.android.com/reference/android/telecom/TelecomManager) unbinds
the `CustomConnectionService` and this shall be null.

### Service definition

In order to create a connection service implementation you need to create a class that inherits from
the `ConnectionService` and `LifecycleOwner` classes. It should implement the required functions
from [`ConnectionService`](https://developer.android.com/reference/android/telecom/ConnectionService)
like `onCreateOutgoingConnection`, `onCreateOutgoingConnectionFailed`, `onCreateIncomingConnection`
and `onCreateIncomingConnectionFailed`. It should also implement `getLifecycle` from
[`LifecycleService`](https://developer.android.com/reference/android/arch/lifecycle/LifecycleService).

[`src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt#L67-L115)

```kotlin
internal class CustomConnectionService : ConnectionService(), LifecycleOwner {
    //...

    override fun onCreateOutgoingConnection(
            connectionManagerPhoneAccount: PhoneAccountHandle?,
            request: ConnectionRequest?
        ): Connection? {
        if (uriAlreadyHasAConnection(request?.address)) {
            tracer.uriAlreadyHasAConnection(request?.address)
            return null
        }
        return OutgoingCustomConnection(request).apply {
            setInitializing()
            setDialing()
            tracer.onCreateOutgoingConnection(this)
        }
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        tracer.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?

    ): Connection? {
        if (uriAlreadyHasAConnection(request?.address)) {
            tracer.uriAlreadyHasAConnection(request?.address)
            return null
        }
        return IncomingCustomConnection(request).apply {
            setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
            setInitializing()
            setRinging()
            tracer.onCreateIncomingConnection(this)
        }
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        tracer.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    //...

    override fun getLifecycle() = dispatcher.lifecycle

    //...
}
```

In this example we have created the `CustomConnectionFacade` class that simulates incoming and
outgoing calls. This class is also able to change the current call's state. Simulated calls can be
created after registering a custom phone account in the system.

[`src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionFacade.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionFacade.kt#L58-L131)

```kotlin
class CustomConnectionFacade(private val context: Context) {
    //...

    // Registers the custom phone account in the system. Needed before calling [createIncomingCall]
    // or [createOutgoingCall].
    private fun registerCustomPhoneAccount(): Boolean {
        //...
        telecomManager.registerPhoneAccount(customPhoneAccount)
        //...
    }

    // Unregisters the custom phone account from the system. The recent calls related to the custom
    // phone account will be deleted as well.
    private fun unregisterCustomPhoneAccount(): Boolean {
        //...
        CustomConnectionServiceHolder.withCustomConnectionService {
            // Clean and destroy all pending connections if there are any.
            it.clearAllConnections()
        }
        telecomManager.unregisterPhoneAccount(customPhoneAccount.accountHandle)
        //...
    }

    // Creates an incoming call using the [customPhoneAccount].
    private fun createIncomingCall(phoneNumber: String) {
        //...
        val extras = Bundle()
        val uri = phoneNumber.toPhoneUri()
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
        telecomManager.addNewIncomingCall(customPhoneAccount.accountHandle, extras)
        tracer.onCreatedIncomingCall(uri)
    }

    // Creates an outgoing call using the [customPhoneAccount].
    private fun createOutgoingCall(phoneNumber: String) {
        //...
        val extras = Bundle()
        val uri = phoneNumber.toPhoneUri()
        extras.putParcelable(
            TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,
            customPhoneAccount.accountHandle
        )
        //...
        telecomManager.placeCall(uri, extras)
        tracer.onCreatedOutgoingCall(phoneNumber.toPhoneUri())
    }

    // Updates a call state.
    fun applyCallState(
        phoneNumber: String,
        callState: CallState,
        disconnectCause: DisconnectCause?
    ) {
        tracer.applyCallState(phoneNumber, callState, disconnectCause)
        CustomConnectionServiceHolder.withCustomConnectionService {
            // Change call's state.
            it.changeConnectionState(phoneNumber, callState, disconnectCause)
        }
        //...
    }

    //...
}
```

### Service lifecycle

To manage the initialization and destruction of the service, override the `onCreate` and `onDestroy`
methods.

When the service is created:

[`src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt#L50-L54)

```kotlin
override fun onCreate() {
    dispatcher.onServicePreSuperOnCreate()
    CustomConnectionServiceHolder.setCustomConnectionService(this)
    super.onCreate()
}
```

The `CustomConnectionServiceHolder` creates and stores an instance of the
`CustomConnectionService`.

When the service is destroyed:

[`src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/examples/telephony/customconnection/src/main/kotlin/com/example/ivi/example/telephony/customconnection/CustomConnectionService.kt#L56-L60)

```kotlin
override fun onDestroy() {
    dispatcher.onServicePreSuperOnDestroy()
    super.onDestroy()
    CustomConnectionServiceHolder.setCustomConnectionService(null)
}
```

The instance of the `CustomConnectionService` is null.

## External links

- TomTom IndiGO [`TelecomService`](TTIVI_PLATFORM_API).
- Android [`ConnectionService`](https://developer.android.com/reference/android/telecom/ConnectionService).

