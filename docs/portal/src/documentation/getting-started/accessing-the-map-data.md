---
title: Accessing the Map Data
---

TomTom IndiGO uses an on-board map for its navigation features, but this map is not included in the
SDK package or in the emulator image. You must download and install the map separately.

## Downloading the map

Under the terms of the
[TomTom IndiGO evaluation license](/tomtom-indigo/documentation/getting-started/introduction)
a few demonstration maps are provided in the
[Artifact Repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).
Click one of these links and download the map of your choice:

- A map of the USA state of
  [Michigan](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Fdigital-cockpit%2Ftomtom-digital-cockpit-maps%2Fusa-michigan).

Each map consists of a keystore file and a tarball with the map data.


## Installing the map

Make sure you have built the `IVI Template App` and you are running it in the
[TomTom IndiGO emulator](/tomtom-indigo/documentation/getting-started/the-tomtom-indigo-emulator#running-the-tomtom-indigo-emulator),
if you are installing the on-board map on an emulator.

<Blockquote>
  The on-board map and keystore file need to be installed for each application individually. In
  these instructions the target application is the IVI Template App, with the package
  name <code>com.example.ivi.template.app</code>. For one of the example apps, use the package name
  from that specific AndroidManifest.xml file.
</Blockquote>

__Windows:__ The `adb` tool comes with the Android SDK, but is not automatically included in the
PATH. You can find it in `%UserProfile%\AppData\Local\Android\Sdk\platform-tools\adb.exe`.

To install a downloaded map on the emulator or on a real device:

- Extract the keystore tarball, then push the keystore file:

```cmd
adb root
adb shell mkdir /data/media/10/Android/data/com.example.ivi.template.app/files/keystore
adb push NK_AUTO_DEV.NKS /data/media/10/Android/data/com.example.ivi.template.app/files/keystore
```

- Extract the downloaded tarball with map data, then push it to the target device:

```cmd
adb push my-directory-with-map-data /data/media/10/Android/data/com.example.ivi.template.app/files/map/
```

- Restart the `IVI Template App`:
  - Swipe up from the bottom of the device's screen.
  - Click on the `All apps` menu icon (grid of nine squares).
  - Scroll down and select `Settings`.
  - Scroll down and select `Apps & notifications`.
  - Select `Show all apps`.
  - Scroll down and select `IVI Template App`.
  - Select `Force stop` on the top right of the screen, then click `OK`.
  - Click on the `All apps` menu icon (grid of nine squares).
  - Scroll down and restart the `IVI Template App`.

If the map and keystore file were succesfully installed, you will now see a blue chevron appear.

![Emulator Blue Chevron](images/emulator_blue_chevron.png)

## Setting the emulator location

The IVI Template App's navigation features only work if the emulator device's location is within
the on-board map's region.

- Change the device's location:
  - Click the three vertical dots in the top bar of the emulator screen. This opens the `Extended
    Controls` dialog.
  - Select `Location` from the menu.
  - In the `Search` bar, enter a location within the on-board map's region, then click `Set
    Location` and close the `Extended Controls` dialog.

The on-board map is now visible in the emulator, and navigation features operational.

![Emulator On-board Map Visible](images/emulator_onboard_map_visible.png)


### Using other map regions

If you need a map for an area not included in these demo maps, please ask your TomTom
representative.

## Next steps

Congratulations, you now have an operational TomTom IndiGO environment.

From here on, you have several options:

- Experience the
  [off-the-shelf features](/tomtom-indigo/documentation/platform-overview/example-apps#off-the-shelf-functionality)
  in the template application.

- Build and execute the example applications, as explained in the
  [Tutorials and Examples](/tomtom-indigo/documentation/tutorials-and-examples/overview) section.

- Use the
  [Debug Menu](/tomtom-indigo/documentation/getting-started/the-debug-menu)
  to explore the TomTom IndiGO platform in more detail. For example, by
  [starting a route demo](/tomtom-indigo/documentation/getting-started/frequently-asked-questions-faq#how-do-i-start-a-route-demo).

- As the TomTom IndiGO platform evolves, you can integrate its newest features by following the
  instructions on the
  [Upgrade to a Newer Version of TomTom IndiGO](/tomtom-indigo/documentation/getting-started/upgrade-to-a-newer-version-of-tomtom-indigo)
  page.

- When you want to learn more about Kotlin or the Android platform, explore the documentation listed
  on the
  [Additional Resources](/tomtom-indigo/documentation/getting-started/additional-resources)
  page.

- If you encounter any issues, please consult our
  [Frequently Asked Questions](/tomtom-indigo/documentation/getting-started/frequently-asked-questions-faq).
