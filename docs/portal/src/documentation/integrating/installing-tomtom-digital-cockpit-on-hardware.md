---
title: Installing TomTom Digital Cockpit on Hardware
---

For installation on hardware and enabling all the functionality, you need to sign the application,
install it on the system partition, and do some additional configurations as explained below.

The TomTom Digital Cockpit SDK comes with a prebuilt emulator image and a prebuilt image for our reference
hardware (Samsung Galaxy S5E tablet), based on version 18.1 of LineageOS, which in turn is based
on Android 11.

To use TomTom Digital Cockpit on your own hardware read through the information below.

## Platform requirements

| Component                       | Minimum                                     | Recommended                                   |
|---------------------------------|---------------------------------------------|-----------------------------------------------|
| CPU                             | Quad-core<br>4.000 DMIPS per core           | Octa-core<br>4500 DMIPS per core              |
| GPU                             | Adreno 330 or better<br>160 GFLOPS (FP32)   | Adreno 615 or equivalent<br>350 GFLOPS (FP32) |
| RAM                             | 4 GB<br>LPDDR4 – 15 GB/S                    | 6 GB<br>LPDDR4 – 15 GB/S                      |
| FLASH (depending on map region) | 40 GB<br>EMMC 5.1                           | 64 GB<br>EMMC 5.1                             |
| O.S.                            | Android Automotive 10                       | Android Automotive 11                         |
| Screen size                     | >12.3″ (landscape) >11.6″ (portrait)        | >12.3″ (landscape) >11.6″ (portrait)          |
| Connectivity                    | WiFi or cellular (3G or better)             | WiFi or cellular (3G or better)               |
| Positioning                     | GNSS + dead reckoning                       | GNSS + dead reckoning                         |
| Bluetooth                       | 4.2                                         | 5.0                                           |
| Microphone                      | Yes<br>Acoustic echo and noise cancellation | Yes<br>Acoustic echo and noise cancellation   |
| Speaker                         | Yes                                         | Yes                                           |

## Automotive vs. plain Android

In principle, TomTom Digital Cockpit can run on any Android device with a
TomTom Digital Cockpit-supported CPU architecture, but the experience will be poorer. If using
non-automotive Android, for example, Bluetooth will not behave as expected. This is because standard
Android uses Headset and Handsfree server profiles, as the mobile phone is the audio source that
goes to for example a Bluetooth headset. Android Automotive instead enables the client profile, as
the car head unit connects to a mobile phone instead, and streams from it. Therefore, do not use
Bluetooth features such as calling, companion app integration, or media streaming.

Also, support for secondary displays like a cluster display and air conditioning controls is
missing.

Android Automotive will support all car-related functionality such as calling, media streaming, and
air conditioning controls.

More limitations are present in certain domains. Read below for more information.

## System signature vs. Root access

To enable some features, especially regarding media, the application needs to be signed and
installed on the system partition. You can read more about privileged and system permissions
[here](https://source.android.com/devices/tech/config/perms-allowlist).

### Sign the TomTom Digital Cockpit APK with the same key that was used for signing the system image

The TomTom Digital Cockpit platform debug key and password files (used for the pre-created tablet and 
emulator images) are in the
[`keystore`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/keystore)
directory in the example source. If you're not using the pre-created tablet or emulator images, you
can simply replace these with your own platform keys and rebuild.

### Then push the APK to the system partition

- Remount the system partition to be writeable.
- Copy the `privapp-permissions-tomtom-ivi.xml` file from the
  [`permissions`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/permissions)
  directory to `/system/etc/permissions` on the device. Don't forget to change the
  [package name](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/permissions/privapp-permissions-tomtom-ivi.xml#L14)
  when installing your own Digital Cockpit app.
- Install the Digital Cockpit APK that you have built, into the `/system/priv-app` directory on the
  device. This can be your own Digital Cockpit app, or it can be built from the Template app in the
  [TomTom Digital Cockpit SDK example sources](/tomtom-digital-cockpit/documentation/getting-started/accessing-tomtom-digital-cockpit-sdk-example-sources).

```cmd
$ adb remount
$ adb push privapp-permissions-tomtom-ivi.xml /system/etc/permissions/
$ adb shell mkdir /system/priv-app/MyDigitalCockpitApp
$ adb push MyDigitalCockpitApp.apk /system/priv-app/MyDigitalCockpitApp/
```

If it's not possible to sign the APK with the system signature, there will be the following
limitations (note that you should still follow the two other steps):

- Media being played by the system will not be visible in the main process panel, nor will it be 
  possible to control it.
- The media source Spotify will never work: TomTom Digital Cockpit needs to be signed with platform keys for
  Spotify to communicate with it.

To work around the first problem, enable media notification access. This fixes access to all media
sources except Spotify.

```cmd
$ adb root
$ adb shell cmd notification allow_listener com.example.mydigitalcockpitapp/com.tomtom.ivi.appsuite.media.plugin.service.media.MediaNotificationListener
```

Spotify only communicates with system apps signed with the platform signature, so if your APK is
unsigned or not a system app, this command will only allow you to control Spotify playback started
with the default AOSP media player, but not to browse Spotify content from within TomTom Digital Cockpit.

## Domain-specific notes

### Text-To-Speech

Since Android Automotive doesn't come with a TTS engine by default, you will need to install a TTS
engine APK for Messaging TTS and Navigation voice instructions to work.

There are many TTS engines available for Android, our reference hardware currently uses the Google
Text-to-Speech application.

### Multimedia sources

TomTom Digital Cockpit supports any Android Automotive media source that can also be found in the Android
Automotive Play Store.

By default TomTom Digital Cockpit does not support advanced functionality, such as custom actions. These add
capabilities unique for a single source, such as liking a song. Support for them can be added via
a TomTom Digital Cockpit plugin.

If a multimedia app requires a sign in, a modal panel allowing you to do so will open when
selecting the app.

If the hardware and AOSP build both support Radio (FM/AM/DAB), the radio multimedia source in
TomTom Digital Cockpit should also work (albeit with a confusing user interface, as no specialization work 
for radio has yet been made).

### Phone

To automatically configure TomTom Digital Cockpit as the default dialer app, set TomTom Digital Cockpit as the 
default dialer in system settings (Settings > Apps & Notifications > Default apps).

Pair a phone with Bluetooth and ensure that all necessary profiles (such as Phone calls, Media
audio, Text messages and Contact Sharing) are enabled by tapping on the phone name in settings.
You should thereafter be able to use this phone from TomTom Digital Cockpit.

