---
title: Configure a TomTom API Key
---

<Blockquote hasIcon>
    This page is applicable only for TomTom IndiGO version 1.0.3880-1630 and earlier.
</Blockquote>

You have received a TomTom API key, which must be used in the example app for the map and
navigation-related features to work. These features use TomTom's NavKit2 libraries.

The TomTom API key can be configured in various ways depending on your development and production
needs:

- [Via `local.properties` or Gradle properties](#via-localproperties-or-gradle-properties).
- [By overriding the static configuration values in Android resources](#by-overriding-the-static-configuration-values-in-android-resources).
- [With a static configuration provider](#with-a-static-configuration-provider).

## Via local.properties or Gradle properties

The easiest way is to set the `navkit2ApiKey` property to your TomTom API key,
in the top-level `local.properties` file or as a Gradle property:

`navkit2ApiKey=<your TomTom API key>`

This option is preferred if you do not want to add the TomTom API key to your Source Code Management
(SCM), for example Git. In this case you can configure the key in the `local.properties` file (which
is not supposed to be added to an SCM) or configure the key in the `gradle.properties` file in the
Gradle user home directory. The latter option makes the key available in any Gradle project.

## By overriding the static configuration values in Android resources

You can override static configuration values in Android resources. See
`template/app/src/main/res/values/navkit2apikeys.xml` for a template and additional instructions.

Disable the build-time check by setting the `disableNavkit2ApiKeyBuildTimeCheck` property to `true`
in the top-level `gradle.properties` file, or by not applying the
[`com.tomtom.ivi.appsuite.gradle.navkit2.api.appsuitedefaults.navkit2.config`](TTIVI_INDIGO_GRADLEPLUGINS_API)
Gradle plugin.

This option is preferred if you want to simplify the project setup. However it requires the key to
be added to your SCM.

## With a static configuration provider

Last but not least is to configure a static configuration provider in the Android Application class
that provides the key. For this you can use a `Navkit2ApiKeyStaticConfigurationProvider` instance.
The `TemplateApplication` class contains an example explaining how to configure it.

Disable the build-time check by setting the `disableNavkit2ApiKeyBuildTimeCheck` property to `true`
in the top-level `gradle.properties` file, or by not applying the
[`com.tomtom.ivi.appsuite.gradle.navkit2.api.appsuitedefaults.navkit2.config`](TTIVI_INDIGO_GRADLEPLUGINS_API)
Gradle plugin.

This option is preferred if you want to obtain and configure the key at runtime.
