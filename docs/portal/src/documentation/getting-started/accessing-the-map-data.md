---
title: Accessing the Map Data
---

<Blockquote>
    When using TomTom IndiGO version 1.0.3880-1630 or earlier, please skip this page and instead go
    to&nbsp;
    <a href="/tomtom-indigo/documentation/getting-started/getting-the-tomtom-api-key-navkit2">
        Getting the TomTom API Key (NavKit2)
    </a>
    .
</Blockquote>

TomTom IndiGO uses an on-board map for its navigation features, but this map is not included in the
SDK package or in the emulator image. You must download and install the map separately.

## Downloading the map

Under the terms of the
[TomTom IndiGO evaluation license](/tomtom-indigo/documentation/getting-started/introduction)
a few demonstration maps are provided in the
[Artifact Repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).
Click one of these links and download the map of your choice:

- A map of
  [France](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-maps/france).
- A map of the USA state of
  [Michigan](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-maps/usa-michigan).

Each map consists of a keystore file and a tarball with the map data.


## Installing the map

To install a downloaded map on the emulator or on a real device:

- Push the keystore file:

```cmd
adb root
adb push NK_AUTO_DEV.NKS /data/media/10/Android/data/com.tomtom.ivi.product.standalone.indigo/files/keystore/
```

- Extract the downloaded tarball with map data, and push it to the target device:

```cmd
adb push my-folder-with-map-data /data/media/10/Android/data/com.tomtom.ivi.product.standalone.indigo/files/map/
```

- Restart the TomTom IndiGO application:
  - Swipe up from the bottom.
  - Go to the menu :::
  - Go to _Settings_ > _Apps and Notifications_
  - Select _IndiGO application_
  - Select _Force stop_ then _Open_

### Using other map regions

If you need a map for an area not included in these demo maps, please ask your TomTom
representative.

__Next step:__
[Configuring Android Studio](/tomtom-indigo/documentation/getting-started/configuring-android-studio).
