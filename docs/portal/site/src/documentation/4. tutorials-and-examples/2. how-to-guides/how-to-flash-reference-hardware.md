---
title: How to Flash the Reference Hardware
layout: default
---

## Tablet specifications

The tablet we are using is a Samsung Galaxy Tab S5e WiFi (SM-T720).
This tablet is available in 2 variants:

- 4GB RAM and 64GB internal storage
- 6GB RAM and 128GB internal storage

Both variants will work with our OTA image but preference should be given to the 128GB (with 6GB RAM)
variant, which allows to run IndiGO / Cluster app on multiple screens without memory issues.

__Note:__
The Galaxy Tab S5e __LTE__ (SM-T725) is not supported by our OTA image!

## Image modifications

The image we flash on the Samsung Galaxy S5E tablet is based on version 18.1 of LineageOS, which
in turn is based on Android 11.
The image is customized for our own use cases, main modifications are as follows:

- Added an IndiGO placeholder system app, to give developers' builds access to system-only
permissions:
  - multimedia browsing
  - virtual display
  - call management
- Replaced the platform key with the IndiGO key, so that IndiGO is recognized as a system app and
is granted system permissions.
- Modified the LineageOS updater app to download and install OTA updates from TomTom's Artifactory
server (this requires VPN access to the TomTom internal network)
- Enabled Bluetooth audio profiles for cars (e.g. the target behaves as BT speakers):
  - A2DP profile in sink mode
  - HandsFree profile in HandsFree mode
  - PBAP client
- Added Android Auto features:
  - android.car.* APIs
  - media browsing

This guide explains how to install and use the customized IndiGO image on a new Samsung Galaxy Tab
S5e tablet.

## Prerequisites

Currently, the __initial__ flashing procedure requires the availability of:

- A Windows machine.
- The `adb` tool, installed on any machine, it doesn't have to be the Windows machine above.

## Step-by-step installation guide

__Note:__ If the tablet has already been flashed with the LineageOS image, it can be upgraded by
following the instructions in [Upgrade Tablet](#upgrade-tablet).

The instructions are based on the official
[LineageOS installation instructions](https://wiki.lineageos.org/devices/gts4lvwifi/install):

1. Download these files:

   - [Odin](https://androidfilehost.com/?fid=4349826312261712202)  (Samsung flashing tool)
   - [TWRP](https://dl.twrp.me/gts4lvwifi/twrp-3.5.0_9-0-gts4lvwifi.img.tar)  (Recovery tool)
   - [Samsung USB drivers](https://developer.samsung.com/mobile/file/4d05badf-eaea-4095-af16-3aabfad74760)
   - [vbmeta.tar](https://dl.twrp.me/gts4lvwifi/vbmeta.tar)

2. Download the latest release of the OTA image (file ending with -`ota.zip)` from
   [here](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Fivi%2Flineageos-gts4lvwifi).

3. Install the Samsung USB drivers and the Odin tool on your Windows machine.

4. Power on the tablet and go through the initial setup if it's the first time it's being powered
   on. You can skip as many steps as you can.

5. Connect the tablet to a WiFi network.

   This step is __\*necessary\*__ to be able to show the __`OEM unlock`__ option below.

6. From the __`Settings`__ app, select __`About tablet`__ → __`Software Information`__. Tap 7 times
   on __`Build number`__ to enable the __`Developer Options`__.

7. From the __`Settings`__ app, enable __`Developer Options`__ → __`OEM unlock`__.

   If the option is not visible, make sure that you have a working WiFi connection and open the
   __`Developer Options`__ menu again.

8. Power off the tablet by long pressing the __`Power`__ button and 
   __`Volume Down`__ button and then tapping on __`Power off`__.

9. Press the __`Power`__, __`Volume Up`__ and __`Volume Down`__ buttons at the same time and,
   while keeping the buttons pressed, plug the USB cable into the Windows machine.
   The tablet should show a blue screen (Samsung's download mode) showing the option to unlock
   the bootloader, as in the image below.

   ![img](images/flash_hw_bootloader.png)

10. Long press the __`Volume Up`__ button to select the __`Unlock bootloader`__ page. The image
    below will be shown:

    ![img](images/flash_hw_unlock_bootloader.png)

11. Press the __`Volume Up`__ button again to finally unlock the bootloader. The device will
    perform a factory reset and restart into Android.

12. Repeat steps 4, 5, 6 and verify that the __`OEM Unlock`__ option is still enabled in the
    __`Developer options`__ menu.

    Ensure that the tablet is connected to WiFi and that the __`OEM unlock`__ option is visible,
    otherwise the next steps might fail!

13. Power off the tablet by long pressing the __`Power`__ button
    and __`Volume Down`__ button and then tapping on __`Power off`__.

14. Press the __`Power`__, __`Volume Up`__ and __`Volume Down`__ buttons at the same time and,
    while keeping the buttons pressed, plug the USB cable into the Windows machine.
    The tablet will show the blue screen (Samsung's download mode) again.

    Alternatively, steps 13 and 14 can be skipped. Enable the __`USB debugging`__ option in the
    __`Developer options`__ menu, and run this command instead: `adb reboot download`.

15. Start the Odin tool that you previously installed on the Windows machine.

16. Check that you see a valid device in the top left corner of the Odin window. It will show up
    as something like __`COM0`__; the number after `COM` can be any.

17. Check the box next to the button labeled __`AP`__, and then click the __`AP`__ button.

18. A pop-up dialog will ask you to select a file. Select the previously downloaded
    [vbmeta.tar](https://dl.twrp.me/gts4lvwifi/vbmeta.tar) file and press the __Start__ button.
    A blue transfer bar will appear on the device showing the __`vbmeta`__ image being flashed.
    After that, the device will reboot.

19. The device will fail to restart; this is expected. It will show the option to perform a factory
    reset. Perform the factory reset by pressing the __`Volume down`__ button followed by the
    __`Power`__ button.
    Confirm the selection by pressing the __`Volume down`__ button followed by the __`Power`__
    button.

20. Repeat steps 12, 13, 14, 15, 16, 17.

21. This time, selects the previously downloaded __`TWRP`__ file when asked to select a file.

22. Press the __Start__ button and keep the __`Volume Up`__  button pressed while the file is
    being flashed and the tablet reboots.
    The tablet should now reboot into recovery: the __`Team Win Recovery Project`__ tool.

    __Note:__ Be sure to reboot into recovery immediately after installing __`TWRP`__. If you
    don’t, the custom recovery will be overwritten on boot.

23. The recovery tool will ask whether to keep System Read Only, as shown in the image below.

    ![img](images/flash_hw_system_readonly.png)

    Check the _Never show this screen during boot again_ box and swipe the bottom bar to allow
    system modifications.

24. Tap __`Wipe`__.

25. Now tap __`Format Data`__ and continue with the formatting process. This will remove
    encryption and delete all files stored in the internal storage.

26. Return to the previous menu and tap __`Advanced Wipe`__, select the __`Cache`__ and
    __`System`__ partitions, and then __`Swipe to Wipe`__.

27. Sideload the LineageOS `.zip` package:

    - On the device, select __`Advanced`__ → __`ADB Sideload`__, then swipe to begin sideload.
    - On the host machine, sideload the package using:
      `adb sideload lineageos-gts4lvwifi-<version>-ota.zip`. This is the file you downloaded from
      [here](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Fivi%2Flineageos-gts4lvwifi).

28. On the device, select __`System`__ → __`Reboot`__. The tablet will now reboot into the custom
    TomTom IndiGO image.

29. Congratulations!

## Upgrade Tablet

If the tablet has already been flashed with the LineageOS image, it can be upgraded with any
machine that has `adb` installed, using the following command:

```bash
adb root
adb push lineageos-gts4lvwifi-<version>-ota.zip /data/lineageos-gts4lvwifi-ota.zip && adb shell "echo --update_package=/data/lineageos-gts4lvwifi-ota.zip > /cache/recovery/command" && adb reboot recovery
```
