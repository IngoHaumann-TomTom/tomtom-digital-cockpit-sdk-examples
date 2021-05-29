---
title: IndiGO SDK getting started guide for Ubuntu 20.04
---

# IndiGO SDK getting started guide for Ubuntu 20.04

This is a step-by-step guide on how to compile and run the IndiGO example application.

First you will need to install Android Studio using OpenJDK 11, get
TomTom Maps API keys and get access to our SDK downloads. Then you will be able to
compile and run your first IndiGO application.

## Install Android Studio

To install the Android Studio IDE, follow the instructions from the [official Android guide](https://developer.android.com/studio/install).

## Install OpenJDK 11

The IndiGO SDK requires OpenJDK 11; other versions may work but are not supported. To install it,
run the following command in a terminal:

```bash
$ sudo apt install openjdk-11-jdk
```

You might already have other versions of JDK installed. Verify this by running:

```bash
$ sudo update-java-alternatives -l
java-1.11.0-openjdk-amd64      1111       /usr/lib/jvm/java-1.11.0-openjdk-amd64
```

If you see the output above, then you only have OpenJDK and you are all set. If you see multiple
Java versions in the output, you need to make sure you have the correct one set as the default:

```bash
$ sudo update-java-alternatives -s java-1.11.0-openjdk-amd64
```

Then you can verify that the correct Java version is in use by running:

```bash
$ java --version
openjdk 11.0.11 2021-04-20
OpenJDK Runtime Environment (build 11.0.11+9-Ubuntu-0ubuntu2.20.04)
OpenJDK 64-Bit Server VM (build 11.0.11+9-Ubuntu-0ubuntu2.20.04, mixed mode, sharing)
```

As you can see above, OpenJDK 11 is in use.

## Download and decompress IndiGO SDK

Download the IndiGO SDK from [Nexus](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk).

Unzip it and you will find another zip file inside of it: the example app source
code.
Unzip that one as well and then follow the following instructions to setup an
environment in which to run IndiGO.

## Running IndiGO

IndiGO can run on Android emulators (AVDs) in addition to the reference / demo
hardware (Samsung Galaxy Tab S5e). A special Android emulator configuration has
been created for running IndiGO. This configuration contains a number of
customizations to the emulator hardware and the Android platform on which it
runs. This emulator can be used for development on top of IndiGO and runs
x86_64 AOSP with Android 11 Automotive.

The following sections describe how to download the correct Android emulator
configuration.

### Installing the IndiGO emulator

In Android Studio, go to `Tools -> SDK Manager -> SDK Update Sites` and add a
new entry with the following name and URL:

> Name: `TomTom IndiGO Update Site - Android 11 Emulators`
>
> URL: `https://repo.tomtom.com/repository/ivi/com/tomtom/ivi/ivi-automotive-sdk/aaos-system-images/center-display/12/center-display-12.xml`

Tick `Use Authentication`, and enter your TomTom Nexus credentials and click
`OK`.

Switch to the `SDK platforms` tab, tick `Show Package Details` on the bottom
right. Then locate the
`Indigo Automotive Android System Image Intel x86 Atom_64 System Image` item in
the list. You may need to expand the Android 11 section to see the right item.

![Android Studio SDK platforms download list](images/android_studio_sdk_platforms_download_list.png "Android Studio SDK platforms download list")

Select the system image and start downloading it.

After the download completes, go to your terminal, change directory to the root
directory of the example app and run:

```bash
$ ./gradlew startDevelopmentEmulator
```

This will create the emulator if it has not been created before and then start
it.

## Installing the example application on a custom device

Some features of the IndiGO example application require system permissions. For instance, you won't be able to play music using Spotify nor use HVAC features. If you want to install the example app on a different emulator or device, you need to make sure that it is signed with the corresponding platform key.

The platform key and password files are in the `keystore` directory. Drop in your platform keys replacing the existing ones in that directory, rebuild and deploy.

## Get API Keys

You should have received API keys from TomTom. Those should be used in the
example app.

[TODO(IVI-3759)]: # (How to use the API keys inside the example app?)

## Compiling the IndiGO example app

The IndiGO example app can be built using Gradle on the command line.
To compile, run the following command from the example app root directory:

```bash
./gradlew build
```

To install the application on all available devices, run:

```bash
./gradlew installDebug
```
