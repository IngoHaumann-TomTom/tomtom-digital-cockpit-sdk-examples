# How to configure the NavKit2 API key

You have received NavKit2 API key(s) from TomTom. These must be used in the example app for the
navigation-related features to work.

The NavKit2 API key(s) can be configured in various ways depending on your development and
production needs:

- [Via a `local.properties` file or via Gradle properties](#via-a-localproperties-file-or-via-gradle-properties).
- [By overriding the static configuration values in Android resources](#by-overriding-the-static-configuration-values-in-android-resources).
- [Or with a static configuration provider](#with-a-static-configuration-provider).

## Via a `local.properties` file or via Gradle properties

The easiest way is to set the `navkit2ApiKey` property to your NavKit2 API in your
top-level `local.properties` file or as Gradle property in your `gradle.properties` file.

The `gradle.properties` file can be found in `~/.gradle/gradle.properties` on Mac/Linux
and in `%UserProfile%\.gradle\gradle.properties` on Windows.

To set the `navkit2ApiKey` in your `gradle.properties` file, add the following:

```
navkit2ApiKey=<your NavKit2 API key here>
```


This option is preferred if you do not want to add the NavKit2 API key to your SCM (Git). In this
case, you can configure the key in your `local.properties` file (which is not supposed to be added to a
SCM) or configure the key in the `gradle.properties` files in the Gradle user home directory. The
latter option makes the key available in any Gradle project.

## By overriding the static configuration values in Android resources

You can override static configuration values in Android resources. See
`./modules/products/exampleapp/src/main/res/values/navkit2apikeys.xml` for a template and
additional instructions.

Set the `disableNavkit2ApiKeyBuildTimeCheck` property to `true` in the top-level
`gradle.properties` file or do not apply the `com.tomtom.ivi.platform.defaults.navkit2` Gradle
plugin to disable the build time check.

This option is preferred if you want to simplify the project setup. However it requires the key to
be added to your SCM.

## With a static configuration provider

Last but not least is to configure a static configuration provider in the Android Application class
that provides the key. For this you can use a `Navkit2ApiKeyStaticConfigurationProvider` instance.
The `ExampleApplication` class contains an example how to configure a
`Navkit2ApiKeyStaticConfigurationProvider` instance.

Set the `disableNavkit2ApiKeyBuildTimeCheck` property to `true` in the top-level
`gradle.properties` file or do not apply the `com.tomtom.ivi.platform.defaults.navkit2` Gradle
plugin to disable the build time check.

This option is preferred if you want to obtain and configure the key at runtime.
