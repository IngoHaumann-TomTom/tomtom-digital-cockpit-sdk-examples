---
title: Configuring Android Studio
---

To install the Android Studio IDE, follow the instructions from the
[official Android guide](https://developer.android.com/studio/install).
For TomTom IndiGO the recommended version is Android Studio version `Chipmunk | 2021.2.1` or newer.

![Android Studio Splash screen](images/android_studio_splash_screen.png)

If you already have this Android Studio version installed, you can proceed to section
[Building with Android Studio](#building-with-android-studio).

When opening Android Studio for the first time, a dialog box `Import Android Studio Settings` may
show. Select `Do not import settings` and click `OK`.

![Import Android Studio settings](images/android_studio_import_settings.png)

## Gradle plugin for Android Studio

During this installation, or at any time after that, Android Studio may propose upgrading its Gradle
plugin:

![Upgrade Gradle Plugin](images/android_studio_upgrade_gradle_plugin.png)

<Blockquote type="warning" hasIcon>
    <b>Ignore this upgrade!</b> The TomTom IndiGO build files specify the exact version of the
    plugin that is needed. The upgrade by Android Studio would change the TomTom IndiGO build files
    into using a wrong and hard-coded version of this plugin.
</Blockquote>

## Building with Android Studio

This section explains how to configure Android Studio for building the
[TomTom IndiGO template app](/tomtom-indigo/documentation/platform-overview/example-apps)
and the
[TomTom IndiGO example apps](/tomtom-indigo/documentation/tutorials-and-examples/overview#example-applications).

Install Android 11 (R) SDK:

- In the `Welcome to Android Studio` dialog, select `More Actions` > `SDK Manager`.

![Android Studio Welcome Dialog SDK Manager](images/android_studio_welcome_sdk_manager.png)

- The `Settings for new projects` dialog appears. In tab `SDK Platforms`, verify whether
  `Android 11.0 (R)` is installed. If not yet installed, enable it and click `Apply`.

![Android Studio SDK Manager Android 11](images/android_studio_sdk_manager_android_11.png)

Now open the TomTom IndiGO example app project:

- In the `Welcome to Android Studio` dialog, select `Open`.

![Android Studio Welcome Dialog Open Project](images/android_studio_welcome_open_project.png)

- When this dialog does not show, you can open the project via `File` > `Open`.

- From your local clone of the
  [TomTom IndiGO Example sources](/tomtom-indigo/documentation/getting-started/accessing-tomtom-indigo-sdk-example-sources),
  select the top-level `build.gradle.kts` file, and click `OK`.

- Android Studio may show a popup `Trust Gradle Project?`, on which you must click the `Trust
  Project` button.

![Trust Gradle Project](images/android_studio_trust_gradle_project.png)

Ensure that Android Studio uses `OpenJDK11`:

- Go to `File` > `Settings` then open the tab
  `Build, Execution, Deployment` > `Build Tools` > `Gradle`.
    - __Mac__: Go to `Android Studio` > `Preferences...` then open the tab
      `Build, Execution, Deployment` > `Build Tools` > `Gradle`.

- Under `Gradle JDK` select the OpenJDK version that you downloaded earlier (normally shown under
  `JAVA_HOME`) and click `OK`.

Gradle Sync should start running automatically, then Android Studio will configure the project and
download its dependencies. If it does not, start it manually by clicking on the Gradle Sync icon.
This step takes several minutes, depending on your internet connection and your PC's hardware
configuration.

![Android Studio - Gradle sync](images/android_studio_gradle_sync.png)

Now you can browse the source code in the IDE.

Build the application by clicking on the `green hammer` icon:

![Android Studio - Build project](images/android_studio_build_project.png)

__Note:__ Building the application may report an error like below. When this happens, open the SDK
Manager via `Tools` > `SDK Manager`, then in tab `SDK Tools` tick `Show Package Details` and
install the required package and/or accept its license terms.

```cmd
License for package Android SDK Build-Tools 30.0.2 not accepted.
```


__Next step:__ [The TomTom IndiGO Emulator](/tomtom-indigo/documentation/getting-started/the-tomtom-indigo-emulator)
