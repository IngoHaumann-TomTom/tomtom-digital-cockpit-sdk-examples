---
title: Frequently Asked Questions (FAQ)
---

Table of content:

- [When building, there are a lot of unresolved dependencies](#when-building-there-are-a-lot-of-unresolved-dependencies)
    - [Missing credentials](#missing-credentials)
    - [Incorrect Nexus repository credentials](#incorrect-nexus-repository-credentials)
- [There is no map and/or search does not work](#there-is-no-map-andor-search-does-not-work)
- [How do I open/close the Debug Menu?](#how-do-i-openclose-the-debug-menu)
- [How do I start a Route Demo?](#how-do-i-start-a-route-demo)
- [I am using a Mac(book) with an M1 chip and cannot start the android emulator](#i-am-using-a-macbook-with-an-m1-chip-and-cannot-start-the-android-emulator)
- [I get an insufficient storage when installing the APK](#i-get-an-insufficient-storage-when-installing-the-apk)
- [The Android emulator is slow at starting](#the-android-emulator-is-slow-at-starting)
- [How do I cold boot the emulator?](#how-do-i-cold-boot-the-emulator)
- [I get RuntimeException: Required support for OpenGL standard derivatives is missing](#i-get-runtimeexception-required-support-for-opengl-standard-derivatives-is-missing)
- [Android Studio does not install the application](#android-studio-does-not-install-the-application)
- [How do I install the APK onto a custom device?](#how-do-i-install-the-apk-onto-a-custom-device)

---

## When building, there are a lot of unresolved dependencies

### Missing credentials

For example, when you get the following compilation errors, then your Nexus credentials may be
stored incorrectly or are missing.

```cmd
Execution failed for task ':buildSrc:compileKotlin'.
> Could not resolve all files for configuration ':buildSrc:compileClasspath'.
   > Could not find com.tomtom.ivi.platform.gradle:api_framework_config:1.0.2264.
     Searched in the following locations:
     etc...
```

Check that you have stored the credentials properly in file `~/.gradle/gradle.properties`.

__Windows:__ File `%UserProfile%\.gradle\gradle.properties`

__Note:__ The username and password should __not__ be surrounded by quotation marks.

__Note:__ On some operating systems, a hash character `#` in the username or password must be
escaped as `\#` to be properly recognized.

If your credentials are properly stored, then the environment variable `GRADLE_USER_HOME` may point
to a directory other than `~/.gradle/`. Check this environment variable and move your
`gradle.properties` file to the correct location. You can check where it points to during the build
when compiling with `--debug` and look in the logs for `Gradle user home:`

__Windows:__ Folder `%UserProfile%\.gradle\`

### Incorrect Nexus repository credentials

If your build output contains `Received status code 401 from server: Unauthorized`, then Gradle does
find the credentials, but the server rejects them. So the credentials are incorrect.

For example:

```cmd
> Could not GET 'https://repo.tomtom.com/repository/ivi/com/tomtom/ivi/gradle/api_plugins_platform/1.0.1859/api_plugins_platform-1.0.1859.pom'. Received status code 401 from server: Unauthorized
```

---

## There is no map and/or search does not work

TomTom IndiGO uses an on-board map for its navigation features, but this map is not included in the
SDK package or in the emulator image. You must download and install the map separately, as explained
in [Accessing the Map Data](/tomtom-indigo/documentation/getting-started/accessing-the-map-data).

The
[demo maps](/tomtom-indigo/documentation/getting-started/accessing-the-map-data#downloading-the-map)
cover a limited area only. Outside this area, TomTom IndiGO does not show map details, nor can it
find a location or calculate a route.

If you need a map for an area not included in these demo maps, please ask your TomTom
representative.

---

## How do I open/close the Debug Menu?

Refer to the explanation on
[The Debug Menu](/tomtom-indigo/documentation/getting-started/the-debug-menu).

---

## How do I start a Route Demo?

A route demo is a simulation of a drive along a currently planned route on the device, this may be
useful to see what happens at the various points along the route, without the need to go for an
actual drive.

To plan a route, long press on any point on the map, and a route will be planned from the current
position, to this location.

In order to allow position simulation, you first need to enable Developer Options in Android:

- On the emulator, go to the `Settings` menu:
    - Press the Home button (the circle in the sidebar outside the emulator screen).
    - Select `Car Launcher as Home` > `Just Once`.
    - Press the Android application icon (nine dots in a grid) at the bottom of the screen.
    - Scroll down and open `Settings`.
- Scroll down and open `System`, then open `About`.
- Find `Build number` and tap this seven times, after which a `You are now a developer!` toast
  appears to confirm that developer options are enabled.

Enable location mocking:

- Go back to the System menu and scroll down to `Developer options`.
- Scroll down and select `Select mock location app`.
- Select `IVI Example`.
- This setting is persistent until you uninstall the application.

Cold boot the emulator, as explained in the
[How to cold boot the emulator?](#how-to-cold-boot-the-emulator) section.

Start a Route Demo:

- Make sure a route has been planned.
- Tap on the chevron (current position indicator). The `Trip Preview` panel is shown.
- Tap on the `Play` button in the `Trip Preview` panel to start the route demo.
- You can change the vehicle speed by selecting the `1x`, `2x`, or `3x` buttons.
- Tap on the chevron again to hide the `Trip Preview` panel.

---

## I am using a Mac(book) with an M1 chip and cannot start the Android emulator

Attempting to start the Android emulator results in:

```cmd
The emulator process for AVD IndiGO_Emulator was killed.
```

and installing the emulator accelerator package from the Android Update Site results in:

```cmd
Your CPU does not support VT-x. Unfortunately, your computer does not support hardware accelerated virtualization.
```

The Apple M1 processor is not an Intel-based chip and the hardware-accelerated virtualization
package does not work on this processor architecture. Special setup is required to get even the
default Android emulator working, so this is not just a TomTom IndiGO-specific problem. Official
Android support for this chip is still in progress.

The previous instructions on how to configure an Android Virtual Device, specify exceptions for
computers with the M1 processor. Please verify that you have followed those exceptions.

---

## I get an insufficient storage error when installing the APK

You may encounter an `INSTALL_FAILED_INSUFFICIENT_STORAGE` error when installing the APK on the
emulator. Ensure that the emulator AVD has sufficient disk storage configured:

- In Android Studio open the AVD Manager via `Tools` > `AVD Manager`.
- Edit the TomTom IndiGO AVD by clicking its pencil icon in the `Actions` column.
- Click `Show Advanced Settings` and scroll down to the `Memory and Storage` section. Ensure these
  fields have at least the following values:
    - `RAM` : `3072 MB`
    - `VM heap` : `192 MB`
    - `Internal Storage` : `4096 MB`

![Android Studio - Emulator Configuration](images/android_studio_emulator_configuration.png)

- Click `Finish`.

Cold boot the emulator, as explained in the
[How to cold boot the emulator?](#how-to-cold-boot-the-emulator) section.

---

## The Android emulator is slow at starting

The emulator may be slow at starting up on some configurations, especially the first time and
especially on an Apple Mac.

Locate the `config.ini` file for the AVD you are using for running TomTom IndiGO in the emulator.
If you have followed the instructions in
[The TomTom IndiGO Emulator](/tomtom-indigo/documentation/getting-started/the-tomtom-indigo-emulator), it should
be at the following location:

__Linux or Mac:__ `~/.android/avd/IndiGO_Test_Device_API_30.avd/config.ini`

__Windows:__ `%UserProfile%\.android\avd\IndiGO_Test_Device_API_30.avd\config.ini`

Otherwise it could be in one of the other `.avd` directories at that location.

When you have found the `config.ini` file, you can try a few things:

- Try disabling sound, as it may cause the emulator to generate a high CPU load:

```cmd
hw.audioInput=no
hw.audioOutput=no
```

- Check that you have sufficient RAM:

```cmd
hw.ramSize=3072
```

---

## How do I cold boot the emulator?

In some situation a cold boot of the emulator is required, for example after changing its
configuration, or as a precaution when unexpected behavior occurs.

To cold boot the emulator, follow these steps:

- If the emulator is running, click the `x` in the upper-right corner of the emulator sidebar.
- In Android Studio go to: `Tools` > `AVD Manager`.
- On the right-hand side of each configured AVD there is a down-arrow that launches a context menu.
  Press the down-arrow (instead of pressing the green Play button), and select `Cold Boot Now`.

![Android Studio TomTom IndiGO Emulator Cold Boot](images/android_studio_emulator_cold_boot.png)

Now wait for the emulator to start up.

---

## I get RuntimeException: Required support for OpenGL standard derivatives is missing

When you launch the application on emulator, it crashes and restarts in a never-ending loop. Look
in the logcat and if you get the following error, you need to set the emulator's OpenGL ES API
level to 3.1:

```cmd
com.tomtom.ivi.example.product.exampleapp E/AndroidRuntime: FATAL EXCEPTION: GLThread 667
    Process: com.tomtom.ivi.example.product.exampleapp, PID: 5705
    java.lang.RuntimeException: Required support for OpenGL standard derivatives is missing.
        at com.tomtom.navkit.map.TomTomNavKitMapJNI.SurfaceAdapter_onSurfaceCreated(Native Method)
        at com.tomtom.navkit.map.SurfaceAdapter.onSurfaceCreated(SurfaceAdapter.java:170)
        at com.tomtom.navkit.map.sdk.MapView$Renderer.onSurfaceCreated(MapView.java:320)
        at android.opengl.GLSurfaceView$GLThread.guardedRun(GLSurfaceView.java:1541)
        at android.opengl.GLSurfaceView$GLThread.run(GLSurfaceView.java:1272)
```

To fix this, set the emulator's OpenGL ES API level to 3.1 and restart the emulator, as explained in
[The TomTom IndiGO emulator device](/tomtom-indigo/documentation/getting-started/the-tomtom-indigo-emulator#the-tomtom-indigo-emulator-device)
under `Set the Emulator's OpenGL` and `Now restart the emulator`.

---

## Android Studio does not install the application

Sometimes Android Studio does not install your new application after a rebuild but instead launches
a previously installed version present on the emulator or target device. To solve this, enable the
package manager to handle installs:

- Open the `Run/Debug Configurations` dialog via `Run` > `Edit Configurations...`.
- In this dialog tick the box `Always install with package manager`.
- Set field `Install Flags` to `-r -g`, to always reinstall the apk (`-r`) and to grant all
  permissions requested in the manifest file (`-g`). See also the
  [ADB Package Manager](https://developer.android.com/studio/command-line/adb#pm) documentation.

Configuration `Run IVI Template app` as delivered with the SDK has these settings pre-configured.
For the example applications you can configure this yourself, if needed.

![Android Studio package manager](images/android_studio_package_manager.png)

---

## How do I install the APK onto a custom device?

Some features of the TomTom IndiGO example application require system permissions. For instance,
you won't be able to play music using Spotify nor use HVAC features. If you want to install the
example app on a different emulator or device, you need to make sure that it is signed with the
corresponding platform key.

The TomTom IndiGO platform debug key and password files, used for the pre-created tablet and
emulator images, are in the
[`keystore`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/keystore)
directory in the example source. Drop in your own platform keys, replacing the existing ones in that
directory, then rebuild and deploy.

---

## Android doesn't allow my application while driving

An IVI application must tag all its activities to be _Distraction Optimized_; see more about
[driver distraction guidelines](https://source.android.com/devices/automotive/driver_distraction/guidelines#do).
To do this, add the `distractionOptimized` meta-data attribute in the `AndroidManifest.xml` for all
activities and activity aliases. See also [`IviActivity`](TTIVI_PLATFORM_API).
