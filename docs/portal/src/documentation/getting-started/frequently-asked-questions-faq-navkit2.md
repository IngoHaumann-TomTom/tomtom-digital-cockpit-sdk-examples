---
title: Frequently Asked Questions (NavKit2)
---

<Blockquote hasIcon>
    This page is applicable only for TomTom IndiGO version 1.0.3880-1630 and earlier.
</Blockquote>

Table of content:

- [There is no map and/or search does not work](#there-is-no-map-andor-search-does-not-work)
    - [Network connection](#network-connection)
    - [TomTom API key](#tomtom-api-key)

---

## There is no map and/or search does not work

### Network connection

When you start the application for the first time, you should see a map of the world as background
to the application. If this is not there, it may mean that the device/emulator cannot connect to a
network.

On the emulator, open the `Settings` and enable `Wi-Fi`. Then cold boot the emulator.  See detailed
instructions in
[The TomTom IndiGO emulator device](/tomtom-indigo/documentation/getting-started/the-tomtom-indigo-emulator#the-tomtom-indigo-emulator-device)
under `Enable wifi` and `Now restart the emulator`.

For a real device, make sure that it has a working Internet connection.

### TomTom API key

Another reason for this behavior may be that you have not configured a TomTom API key, or you are
using an API key with limited authorizations.

For more details see
[Getting the TomTom API key](/tomtom-indigo/documentation/getting-started/getting-the-tomtom-api-key).
