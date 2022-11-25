---
title: Localize Strings
layout: default
---

The [Android resource framework](https://developer.android.com/guide/topics/resources/providing-resources)
provides [localization support](https://developer.android.com/guide/topics/resources/localization).
On top of the standard Android resource framework, TomTom Digital Cockpit provides additional tooling and
additional metadata to facilitate localization.

## Example application

To try it out, see the
[`examples/examples_localizestrings`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/localizestrings)
application. This example project uses the default frontends and services from the TomTom Digital Cockpit
platform and app suite. It also has the above mentioned Gradle plugin applied. Run:

```cmd
./gradlew :examples_localizestrings_app:extractStringSources
```

The above command will download and extract all `strings.xml` files from modules in the artifact
group `com.tomtom.uxstrings` used by the default frontends and services from the TomTom Digital Cockpit
platform and app suite. After running the above command, you can find the downloaded files in the
`examples/localizestrings/app/build/string-sources` directory.

From these files, you can create your own localized strings. The example project demonstrates this
for the `Media` text in the menu bar:

- The string in
  [`src/main/res/values-fr-rCA/strings.xml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/localizestrings/app/src/main/res/values-fr-rCA/strings.xml#L3)
  is used when the Android language is set to French (Canada).
- The string in
  [`src/main/res/values-fr-rFR/strings.xml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/localizestrings/app/src/main/res/values-fr-rFR/strings.xml#L3)
  is used when the Android language is set to French (France) or other regions, like French (Algeria).
- The string in
  [`src/main/res/values-nl/strings.xml`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/localizestrings/app/src/main/res/values-nl/strings.xml#L3)
  is used when the Android language is set to Dutch, no matter which region.

The British English version of the strings and the default value come included with the Digital Cockpit
platform, so these are not included in the example application.

See [Localize string resources](#localize-string-resources) to provide additional localizations.

## Android resource framework localization support

### Localize string resources

To localize strings see the
[Android localization documentation](https://developer.android.com/guide/topics/resources/localization).

In a nutshell, provide alternative string resource values in a `res/values-<qualifier>/strings.xml`
file per locale. See table 2 of [Providing Alternative Resources](https://developer.android.com/guide/topics/resources/providing-resources#AlternativeResources])
for more information about the locale qualifier.

See [Android String resources documentation](https://developer.android.com/guide/topics/resources/string-resource)
for more information about how to define a string resource.

### Using localized strings resources

See [Language and locale resolution overview](https://developer.android.com/guide/topics/resources/multilingual-support)
and [Testing localized apps](https://developer.android.com/guide/topics/resources/localization#testing).

## Additional tooling and metadata to facilitate localization

To facilitate (additional) localizations, TomTom Digital Cockpit platform provides additional tooling
to discover the strings used in the IVI system. For strings, that can be visible to the end user,
additional metadata is provided to provide additional context. The context describes how the string
is used and where.

### Download string resource source files

String resources are defined in `strings.xml` source files. You can download and extract the
`strings.xml` sources files from dependencies used in your Android Application by running a Gradle
task. This requires the `com.tomtom.tools.android.extractstringsources` Gradle plugin to be applied
in your Android Application Gradle subproject. In this tutorial we refer to the Android Application
Gradle subproject with `app`.

To apply the Gradle plugin define the following in your
[`app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/localizestrings/app/build.gradle.kts#L16)
file:

```kotlin
plugins {
    id("com.tomtom.tools.android.extractstringsources")
}
```

For this to work, you need to configure the version of this Gradle plugin. Add the following in
your top-level
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/build.gradle.kts#L43)
file:

```kotlin
plugins {
   id("com.tomtom.tools.android.extractstringsources") apply false
}
```

And a dependency to `com.tomtom.tools.android:gradle-plugins:<version>` in your
[`buildSrc/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/buildSrc/build.gradle.kts#L29)
file. For example:

```kotlin
dependencies {
    implementation(iviDependencies.gradlePluginTomtomTools)
}
```

Next you can run Gradle task `:app:extractStringSources`. By default this will download and extract
all `strings.xml` files from modules in the artifact group `com.tomtom.uxstrings` based on the
dependencies used in your Android application. You can find the files in `app/build/string-sources`.
Usually the following files are generated:

- Files `app/build/string-sources/<group>:<module>:<version>/values/strings.xml` contain the string
  resource definitions used in the default local (U.S. English).
- Files `app/build/string-sources/<group>:<module>:<version>/values-en-rGB/strings.xml` contain the
British English localizations of the same strings.

__Note:__ When a language-and-region string (for example `values-en-rUS`) is not provided, Android
looks for a string with the correct language (for example `values-en` or `values-en-rUK`) before it
falls back to the default string (read from `values`).

TomTom defines strings in British English. If the U.S. English localization for a string is not
(yet) provided by TomTom, Android will use the British English string definition and not the default
string.

### Download string resource source files configuration

Without any additional configuration, Gradle task `extractStringSources` downloads and extracts
all `strings.xml` files from modules in the artifact group `com.tomtom.uxstrings`. You can configure
the plugin to download and extract `strings.xml` files from other Android Library _source archives_
by configuring the plugin in the `app/build.gradle.kts` file. For example:

```kotlin
ttExtractStringSources {
    includeComponentIds.add("somegroup:somelibrary:*")
}
```

The above configures Gradle task `:app:extractStringSources` to download and extract the
`strings.xml` files from all modules in the artifact group `com.tomtom.uxstrings` and from the
library `somelibrary` in the `somegroup` artifact group. For this to work, the Android libraries
have to publish the `strings.xml` files as part of their sources archive (`*-sources.jar`).

### Additional metadata

String resources defined in TomTom Digital Cockpit, which are visible to the end user, have additional
metadata. This provides additional context per string resource to facilitate translations. For
example:

```xml
<resources xmlns:tomtom="http://www.tomtom.com/metadata">
    <string name="ttivi_bluetoothconnectivity_availabledevices"
      tomtom:description="Shown while searching for Bluetooth devices when at least one device is available."
      tomtom:reviewed="yes"
      tomtom:translate="yes"
      tomtom:use="Text">Available devices</string>
    <!-- ... -->
</resources>
```

In the above example, a string resource `ttivi_bluetoothconnectivity_availabledevices` with text
`Available devices` is defined. The `tomtom:` namespace attributes are added to provide more
context. The following table describes each attribute:

| Attribute | Description |
| --------- | ----------- |
| `description` | A description to give context as to where and how the string is used. |
| `reviewed` | `yes` or `no` to indicate if the string text has been reviewed by TomTom. |
| `translate` | `yes` or `no` to indicate if the string should be translated. For some strings, such as strings defining a product name, it does not make sense to translate them. |
| `use` | Short description of the UI element, see the following table for the possible values. |

Possible values for the `use` attribute:

| Value | UI elements | Examples |
| ------| ----------- | -------- |
| `ASR-TTS` | ASR (commands), TTS (prompts) | |
| `Button` | Small buttons | `Done`, `Close`, `Call` |
| `MenuIcon` | Menu items | `Nav`, `Media` |
| `Message` | Flash messages, warnings questions, explanations, etc. | |
| `Option` | Check boxes, radio buttons | |
| `Text` | Text | |
| `Title` | Screen titles | |
| `Unit` | Any unit of measurement | "km", "miles" |
