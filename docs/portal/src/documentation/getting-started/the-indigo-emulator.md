---
title: The TomTom IndiGO Emulator
---

TomTom IndiGO can run on Android emulators (Android Virtual Devices, AVDs) in addition to the 
reference or demo hardware (Samsung Galaxy Tab S5e). A special Android emulator configuration has 
been created for running TomTom IndiGO. This configuration contains a number of customizations to 
the emulator hardware and the Android platform on which it runs. This emulator can be used for 
development on top of TomTom IndiGO and runs x86\_64 AOSP with Android 11 Automotive.

__Note:__ The TomTom IndiGO platform also runs on a standard Android device, with a TomTom 
IndiGO-supported CPU architecture. However, some features may not work as TomTom IndiGO is 
implemented for the Android Automotive variant.

## The TomTom IndiGO emulator image

Follow these steps to install the TomTom IndiGO emulator image in Android Studio:

- Copy the file `docs/resources/devices.xml` (from the `examples` source), to your `~/.android/`
  folder.

    __Windows:__ Copy to your `%UserProfile%\.android\` folder.

- If Android Studio is running, restart it, so that the new file is picked up.

- Open the SDK Manager via `Tools` > `SDK Manager`.

- Select tab `SDK Update Sites` and add a new entry:
    - Name: `TomTom IndiGO - Android 11 Emulators`
    - URL: `https://aaos.blob.core.windows.net/indigo-automotive/repo-sys-img.xml`
    - Leave `Use Authentication` unchecked, and click `OK`.

- Add another `SDK Update Sites` entry:
    - Name: `TomTom IndiGO - Artifact Repository`
    - URL: `https://repo.tomtom.com/`
    - Tick `Use Authentication`, enter your TomTom
      [repository credentials](/indigo/documentation/getting-started/introduction)
      and click `OK`.

- __Note__ the `s` in `https://`, because Android Studio proposes a default URL as `http://`.

- Click `Apply` to activate these update sites.
    - The `TomTom IndiGO - Android 11 Emulators` entry should not show an error.
    - The `TomTom IndiGO - Nexus repository` entry will show an error, which you can ignore.

![Android Studio SDK Update Sites](images/android_studio_sdk_update_sites.png)

- Switch to tab `SDK platforms` and tick `Show Package Details` on the bottom right.

- Expand the `Android 11 (R)` section, enable the `Indigo Automotive Android System Image` and click
  `Apply` to start downloading this image.

![Android Studio - SDK download list](images/android_studio_sdk_platforms_download_list.png)

- When the download completes, click `Finish` and close the SDK Manager.

## The TomTom IndiGO emulator device

After having downloaded the [emulator image](#the-tomtom-indigo-emulator-image), you can create an
Android Virtual Device (AVD) with it:

- Open the AVD Manager via `Tools` > `AVD Manager`:

![Android Studio - Create Virtual Device](images/android_studio_your_virtual_devices.png)

- Click `Create Virtual Device`. You should now see a list of device definitions.

- In the `Automotive` category, select item `IndiGO_Test_Device`, then click `Next`:

![Android Studio - Select Hardware](images/android_studio_select_hardware.png)

- In the `System Image` dialog, go to tab `x86 Images` and select the upper entry:
    - `Release Name` : `R`
    - `API level` : `30`
    - `ABI` : `x86_64`
    - `Target` : `Android 11.0 (Indigo Automotive Android System Image)`
    - __Note:__ The entries displayed differ only in the last part of their `Target` field.

- __Mac _with_ an M1 processor:__ Go to tab `Other Images` and select `Android S` for `arm64-v8a` as
  the System Image. This is a standard Android image, without TomTom IndiGO-specific customizations 
  or Android Automotive features!

![Android Studio - System Image](images/android_studio_android_r.png)

- Click `Next`. Then, in the `Verify Configuration` dialog, click `Show Advanced Settings` and
  scroll down to the `Memory and Storage` section. Ensure these fields have at least the following
  values:
    - `RAM` : `3072 MB`
    - `VM heap` : `192 MB`
    - `Internal Storage` : `2048 MB`

![Android Studio - Emulator Configuration](images/android_studio_emulator_configuration.png)

- Click `Finish`.

- You now see a new TomTom IndiGO emulator device listed in the AVD Manager:

![Android Studio - TomTom IndiGO Emulator Device](images/android_studio_indigo_emulator_device.png)

- Click the triangular `play` icon on the right to start the emulator.
- When the emulator has started, it shows as:

![Android Studio - TomTom IndiGO Emulator Window](images/android_studio_indigo_emulator_window.png)

- Enable wifi, to ensure that map data can be downloaded:
    - Click the Home button (circle) in the sidebar next to the emulator screen.
    - Press the Android Applications icon (nine dots in a grid) on the bottom of the screen.
    - Scroll down and open `Settings`.
    - Select `Network & internet`.
    - Enable `Wi-Fi` by pressing the toggle button so it turns blue.

- Set the emulator's OpenGL ES API level to 3.1:
    - Click the three dots at the bottom in the sidebar next to the emulator screen. This opens the
      `Extended Controls` dialog.
    - Select `Settings` at the left.
    - Select the `Advanced` tab at the top.
    - This should say `Desktop native OpenGL` and `Renderer maximum (up to OpenGL ES 3.1)`, see
      picture below.
    - Close the `Extended Controls` dialog.

![Emulator - OpenGL Settings](images/emulator_opengl_settings.png)

- Now restart the emulator, for the new settings to take effect:
    - Click the `x` in the upper-right corner of the emulator sidebar.
    - In Android Studio go to: `Tools` > `AVD Manager`.
    - On the right-hand side of each configured AVD there is a down-arrow that opens a context menu.
      Press the down-arrow (instead of pressing the green Play button), and select `Cold Boot Now`.

![Android Studio TomTom IndiGO Emulator Cold Boot](images/android_studio_indigo_emulator_cold_boot.png)

- Close the AVD Manager and wait for the emulator to start up.

- In Android Studio, build and run the application by clicking on the green `play` icon. Select the
  TomTom IndiGO automotive emulator that was created earlier in this tutorial, as the device that 
  the application will run on:

![Android Studio - Build and run](images/android_studio_build_and_run.png)

- When the application starts, you must select `IVI Example` as the default phone app.

![Emulator Default Phone App](images/emulator_default_phone_app.png)

- You will now see the TomTom IndiGO home screen. Note that the map data is shown only when you have
  [configured a valid TomTom API key](/indigo/documentation/getting-started/getting-the-tomtom-api-key).

![TomTom IndiGO Home Screen](images/indigo_home_screen.png)

If you encounter any issues, please consult our
[Frequently Asked Questions](/indigo/documentation/getting-started/frequently-asked-questions-faq).
