# How to configure the NavKit2 API key

You have received NavKit2 API key(s) from TomTom. These must be used in the example app for the
navigation-related features to work.

The NavKit2 API key(s) can be configured is various way depending on your development and
production needs:

- [Via local.properties or Gradle properties](#via-localproperties-or-gradle-properties).
- [By overriding the static configuration values in Android resources](#by-overriding-the-static-configuration-values-in-android-resources).
- [Or with a static configuration provider](#with-a-static-configuration-provider).

## Via local.properties or Gradle properties

The easiest way it to set the `navkit2ApiKey` property to your NavKit2 API in the
top level `local.properties` file or as Gradle property.

This option is preferred if you do not want to add the NavKit2 API key to your SCM (Git). In this
case you can configure the key in `local.properties` file (which is not supposed to be added to a
SCM) or configure the key in the `gradle.properties` files in the Gradle user home directory. The
latter option makes the key available in any Gradle project.

## By overriding the static configuration values in Android resources

You can override static configuration values in Android resources. See
`./modules/products/exampleapp/src/main/res/values/navkit2apikeys.xml` for a template and
additional instructions.

Set the `disableNavkit2ApiKeyBuildTimeCheck` property to `true` in the top level
`gradle.properties` file or do not apply the `com.tomtom.ivi.platform.defaults.navkit2` Gradle
plugin to disable the build time check.

This option is preferred if you want to simplify the project setup. However it requires the key to
be added to your SCM.

## With a static configuration provider

Last but not least is to configure a static configuration provider in the Android Application class
that provides the key. For this you can use a `Navkit2ApiKeyStaticConfigurationProvider` instance.
The `ExampleApplication` class contains an example how to configure a
`Navkit2ApiKeyStaticConfigurationProvider` instance.

Set the `disableNavkit2ApiKeyBuildTimeCheck` property to `true` in the top level
`gradle.properties` file or do not apply the `com.tomtom.ivi.platform.defaults.navkit2` Gradle
plugin to disable the build time check.

This option is preferred if you want to obtain and configure the key at runtime.

## Copyright

Copyright © 2020 TomTom NV. All rights reserved.

This software is the proprietary copyright of TomTom NV and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate license agreement
between you and TomTom NV. If you are the licensee, you are only permitted to use this software in
accordance with the terms of your license agreement. If you are not the licensee, then you are not
authorized to use this software in any manner and should immediately return or destroy it.
