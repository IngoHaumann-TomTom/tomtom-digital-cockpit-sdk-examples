---
title: Flashing the Reference Hardware
---

## Tablet specifications

The tablet we are using is a Samsung Galaxy Tab S5e WiFi (SM-T720).
This tablet is available in 2 variants:

- 4GB RAM and 64GB internal storage
- 6GB RAM and 128GB internal storage

Both variants will work with our OTA image but preference should be given to the 128GB
(with 6GB RAM) variant, which allows to run TomTom Digital Cockpit / Cluster app on multiple screens without
memory issues.

__Note:__ The Galaxy Tab S5e __LTE__ (SM-T725) is not supported by our OTA image!

## Image modifications

The image we flash on the Samsung Galaxy S5E tablet is based on version 18.1 of LineageOS, which
in turn is based on Android 11.
The image is customized for our own use cases, main modifications are as follows:

- Added a TomTom Digital Cockpit placeholder system app, to give developer builds access to system-only
  permissions:

  - multimedia browsing
  - virtual display
  - call management

- Replaced the platform key with the TomTom Digital Cockpit key, so that TomTom Digital Cockpit is recognized as a
  system app and is granted system permissions.
- Modified the LineageOS updater app to download and install OTA updates from TomTom's Artifactory
  server (this modification is for internal use only)
- Enabled Bluetooth audio profiles for cars (for example, the target behaves as BT speakers):

  - A2DP profile in sink mode
  - HandsFree profile in HandsFree mode
  - PBAP client

- Added Android Auto features:

  - android.car.* APIs
  - media browsing

This guide explains how to install and use the customized TomTom Digital Cockpit image on a new Samsung
Galaxy Tab S5e tablet.

## Prerequisites

Currently, the __initial__ flashing procedure requires the availability of:

- A Windows machine.
- The `adb` tool, installed on any machine, it doesn't have to be the Windows machine above.

## Step-by-step installation guide

__Note:__ If the tablet has already been flashed with the LineageOS image, it can be upgraded by
following the instructions in [Upgrade Tablet](#upgrade-tablet).

<Blockquote type="warning" hasIcon>
    TomTom takes no responsibility for any damage that may occur from installing or using the 
    following freely available third-party tools and files.
</Blockquote>

The instructions are based on the official
[LineageOS installation instructions](https://wiki.lineageos.org/devices/gts4lvwifi/install):

To prepare, download the following files:

- [Odin (Samsung flashing tool)](https://androidfilehost.com/?fid=4349826312261712202)
- [TWRP (Team Win Recovery Tool)](https://dl.twrp.me/gts4lvwifi/) (download the latest `img.tar` file)
- [Samsung USB drivers](https://developer.samsung.com/mobile/file/4d05badf-eaea-4095-af16-3aabfad74760)
- [vbmeta.tar](https://dl.twrp.me/gts4lvwifi/vbmeta.tar)

Then, follow these steps:

1. Download the latest release of the OTA image (file ending with -`ota.zip)` from
   [here](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Fivi%2Flineageos-gts4lvwifi).

2. Install the Samsung USB drivers and the Odin tool on your Windows machine.

3. Power on the tablet and go through the initial setup if it's the first time it's being powered
   on. You can skip as many steps as you can.

4. Connect the tablet to a WiFi network.

   This step is ___necessary___ for showing the `OEM unlock` option below.

5. From the `Settings` app, select `About tablet` → `Software Information`, then tap 7
   times on `Build number` to enable the `Developer Options`.

6. From the `Settings` app, enable `Developer Options` → `OEM unlock`. If this option
   is not visible, make sure that you have a working WiFi connection and open the
   `Developer Options` menu again.

7. Power off the tablet by long pressing the `Power` button and `Volume Down` button
   together, and then tapping on `Power off`.

8. Press the `Power`, `Volume Up` and `Volume Down` buttons at the same time and,
   while keeping the buttons pressed, plug the USB cable into the Windows machine.
   The tablet should show a blue screen (Samsung's download mode) showing the option to unlock
   the bootloader, as in the image below.

   ![Flash hardware bootloader](images/flash_hw_bootloader.png)

9. Long press the `Volume Up` button to select the `Unlock bootloader` page. The image
   below will be shown:

   ![Flash hardware unlock bootloader](images/flash_hw_unlock_bootloader.png)

10. Press the `Volume Up` button again to finally unlock the bootloader. The device will
    perform a factory reset and restart into Android.

11. Repeat steps 3, 4, 5 and verify that the `OEM Unlock` option is still enabled in the
    `Developer options` menu.

12. Ensure that the tablet is connected to WiFi and that the `OEM unlock` option is
    visible, otherwise the next steps might fail!

13. Power off the tablet by long pressing the `Power` button
    and `Volume Down` button and then tapping on `Power off`.

14. Press the `Power`, `Volume Up` and `Volume Down` buttons at the same time and,
    while keeping the buttons pressed, plug the USB cable into the Windows machine.
    The tablet will show the blue screen (Samsung's download mode) again.

15. Alternatively, steps 13 and 14 can be skipped. Enable the `USB debugging` option in
    the `Developer options` menu, and run this command instead: `adb reboot download`.

16. Start the Odin tool that you previously installed on the Windows machine.

17. Check that you see a valid device in the top left corner of the Odin window. It will show up
    as something like `COM1`; the number after `COM` can be any.

18. Check the box next to the button labeled `AP`, and then click the `AP` button.

19. A pop-up dialog will ask you to select a file. Select the previously downloaded
    [vbmeta.tar](https://dl.twrp.me/gts4lvwifi/vbmeta.tar) file and press the __Start__ button.
    A blue transfer bar will appear on the device showing the `vbmeta` image being flashed.
    After that, the device will reboot.

20. The device will fail to restart; this is expected. It will show the option to perform a factory
    reset. Perform the factory reset by pressing the `Volume down` button followed by the
    `Power` button.

21. Confirm the selection by pressing the `Volume down` button followed by the `Power` button.

22. Repeat steps 11, 12, 13, 14, 15, 16, 17, 18.

23. This time, when asked to select a file, select the previously downloaded `TWRP` file.

24. Press the __Start__ button and keep the `Volume Up`  button pressed while the file is
    being flashed and the tablet reboots.<br/>
    The tablet should now reboot into recovery: the `Team Win Recovery Project` tool (`TWRP`).<br/>
    __Note__: Be sure to reboot into recovery immediately after installing `TWRP`. If you don’t, the
    custom recovery will be overwritten on boot.

25. The recovery tool will ask whether to keep System Read Only, as shown in the image below.

    ![Flash hardware system readonly](images/flash_hw_system_readonly.png)

    Check the __Never show this screen during boot again__ box and swipe the bottom bar to allow
    system modifications.

26. Tap `Wipe`.

27. Now tap `Format Data` and continue with the formatting process. This will remove
    encryption and delete all files stored in the internal storage.

28. Return to the previous menu and tap `Advanced Wipe`, select the `Cache` and
    `System` partitions, and then `Swipe to Wipe`.

29. Sideload the LineageOS `.zip` package:
    - On the device, select `Advanced` → `ADB Sideload`, then swipe to begin sideload.
    - On the host machine, sideload the package using:
      `adb sideload lineageos-gts4lvwifi-<version>-ota.zip`<br/>
      This is the file you downloaded from
      [here](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Fivi%2Flineageos-gts4lvwifi).

30. On the device, select `System` → `Reboot`. The tablet will now reboot into the custom
    TomTom Digital Cockpit image.

31. Congratulations!

## Upgrade Tablet

If the tablet has already been flashed with the LineageOS image, it can be upgraded with any
machine that has `adb` installed, using the following command:

```cmd
adb root
adb push lineageos-gts4lvwifi-<version>-ota.zip /data/lineageos-gts4lvwifi-ota.zip && \
  adb shell "echo --update_package=/data/lineageos-gts4lvwifi-ota.zip > /cache/recovery/command" && \
  adb reboot recovery
```
