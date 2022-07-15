---
title: Accessing the Map Data
---

<Blockquote>
    When using TomTom IndiGO version 1.0.3880-1630 or earlier, you should have followed the
    instructions on&nbsp;
    <a href="/tomtom-indigo/documentation/getting-started/getting-the-tomtom-api-key-navkit2">
        Getting the TomTom API Key (NavKit2)
    </a>
    .
    Skip the instructions on this page, and go directly to the&nbsp;
    <a href="/tomtom-indigo/documentation/getting-started/accessing-the-map-data#next-steps">
        Next steps
    </a>
    &nbsp;section.
</Blockquote>

TomTom IndiGO uses an on-board map for its navigation features, but this map is not included in the
SDK package or in the emulator image. You must download and install the map separately.

## Downloading the map

Under the terms of the
[TomTom IndiGO evaluation license](/tomtom-indigo/documentation/getting-started/introduction)
a few demonstration maps are provided in the
[Artifact Repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).
Click one of these links and download the map of your choice:

- A map of the USA state of
  [Michigan](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-maps/usa-michigan).

Each map consists of a keystore file and a tarball with the map data.


## Installing the map

To install a downloaded map on the emulator or on a real device:

- Push the keystore file:

```cmd
adb root
adb push NK_AUTO_DEV.NKS /data/media/10/Android/data/com.example.ivi.template.app/files/keystore
```

- Extract the downloaded tarball with map data, and push it to the target device:

```cmd
adb push my-folder-with-map-data /data/media/10/Android/data/com.example.ivi.template.app/files/map/
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
