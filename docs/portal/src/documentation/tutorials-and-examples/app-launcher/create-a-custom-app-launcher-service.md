---
title: Create a Custom App Launcher Service
---

The TomTom IndiGO platform offers a stock implementation of an App Launcher service which provides
the list of the device's currently installed third party Android apps, whilst also allowing these
apps to be launched within TomTom IndiGO.

The app launcher's Android app handling can be replaced with custom behavior. This may for example
be required when:

- There is a requirement to integrate with a particular Android Automotive app store.
- The app launcher needs to display a fixed set of pre-installed apps.

It is also possible to extend the app launcher's functionality to support custom, non-Android, app
types. An example of a custom app type can be a _Web App_, where each app represents a URL and can
be launched by displaying the content in a
[`WebView`](https://developer.android.com/reference/android/webkit/WebView) within a panel.

App launcher app support can be replaced or extended by implementing the following discoverable IVI
services:

- [`AppSourceProviderService`](TTIVI_PLATFORM_API): provides the list of apps available to display
  in the App Launcher.
- [`AppLaunchHandlerService`](TTIVI_PLATFORM_API): handles launching instances of the different app
  types.
- [`AppLaunchTriggerService`](TTIVI_PLATFORM_API): triggers the launching of different app types
  from an external source, like an App Store.

The following sections show how to perform these tasks:
- [Replace the stock Android app support](/tomtom-indigo/documentation/tutorials-and-examples/app-launcher/replace-the-stock-android-app-support)
- [Add support for a custom non-Android app type](/tomtom-indigo/documentation/tutorials-and-examples/app-launcher/add-support-for-a-custom-non-android-app-type)
- [Add support for web app launch trigger](/tomtom-indigo/documentation/tutorials-and-examples/app-launcher/add-support-for-web-app-launch-trigger)



