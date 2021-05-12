---
title: IndiGO SDK getting started guide for Ubuntu 20.04
---

# IndiGO SDK getting started guide for Ubuntu 20.04

This is a step-by-step guide on how to compile and run the IndiGO example application.

First you will need to install Android Studio paired with OpenJDK 11, get TomTom Maps API keys and get access to our SDK downloads. Then you will be able to compile and run your first IndiGO application.

## Install Android Studio

Follow the instructions as found on the [official Android guide](https://developer.android.com/studio/install).

## Install OpenJDK 11

The IndiGO SDK requires OpenJDK 11; other versions may work but are not supported. To install it, run the following command on a terminal:

```bash
$ sudo apt install openjdk-11-jdk
```

You might already have other versions of JDK installed. Verify it by running:

```bash
$ sudo update-java-alternatives -l
java-1.11.0-openjdk-amd64      1111       /usr/lib/jvm/java-1.11.0-openjdk-amd64
```

If you see the output above, then you only have OpenJDK and your are all set. If you see multiple java versions as output, you need to make sure you have the correct one set as default:

```bash
$ sudo update-java-alternatives -s java-1.11.0-openjdk-amd64
```

Then you can verify that the correct java version is in use by running:

```bash
$ java --version
openjdk 11.0.11 2021-04-20
OpenJDK Runtime Environment (build 11.0.11+9-Ubuntu-0ubuntu2.20.04)
OpenJDK 64-Bit Server VM (build 11.0.11+9-Ubuntu-0ubuntu2.20.04, mixed mode, sharing)
```

As you can see above, OpenJDK 11 is in use.

## Installing the IndiGO emulator

On Android Studio, go into Tools -> SDK Manager -> SDK Update Sites and add a new entry with the following information:

> Name: `TomTom IndiGO Update Site - Android 11 Emulators`
>
> URL: `https://repo.tomtom.com/repository/ivi/com/tomtom/ivi/ivi-automotive-sdk/aaos-system-images/center-display/12/center-display-12.xml`

Enable `Use Authentication` and enter your TomTom Nexus credentials.

Switch to the SDK platforms tab, click show package details on the bottom right, then click on "Indigo Automotive Android System Image Intel x86 Atom_64 System Image" and confirm to start the download process.

After the download completes, on the example app directory, run:

```bash
$ ./gradlew startDevelopmentEmulator
```

It will create the emulator if not created yet, then run it.

## Get API Keys

You should have received API keys from TomTom. Those should be used in the example app.

[TODO(IVI-3759)]: # (How to use the API keys inside the example app?)

## Download and decompress IndiGO SDK

Download the IndiGO SDK from [Nexus](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk).

Unzip it and you will find another zip inside of it, the example app source code. Unzip that one as well and you are set to compile the example app.

## Compiling IndiGO example app

Follow instructions in [README.md](../README.md).