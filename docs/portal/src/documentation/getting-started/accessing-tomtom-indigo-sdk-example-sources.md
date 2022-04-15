---
title: Accessing TomTom IndiGO SDK Example Sources
---

The example sources that come with the TomTom IndiGO platform contain the source code that goes 
with the tutorials, to get you started with developing for TomTom IndiGO. The applications also 
allow you to verify your build environment by making sure Gradle can build a TomTom IndiGO example 
APK and install it on an emulator.

When you provided the [signed EA](/tomtom-indigo/documentation/getting-started/introduction), you 
would have received instructions on how to access our GitHub SDK example repository.

The latest example sources for the TomTom IndiGO platform can be downloaded from GitHub:
https://github.com/tomtom-internal/tomtom-indigo-sdk-examples

Run the following command to download the sources:

```cmd
git clone git@github.com:tomtom-internal/tomtom-indigo-sdk-examples.git
```

To see the latest source, run the following command:

```cmd
git checkout main
```

The source is tagged for each release of the SDK. To see the source specific to a version, run the 
following command:

```cmd
git checkout tags/<sdk-version>
```

Precompiled binaires of the example sources and a license compliance report for the platform can 
be found on 
[Nexus](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk).

__Note:__ The following step is not required to be able to build the example sources. If you want 
you can skip ahead to the instructions in the next steps to setup an environment in which to run 
TomTom IndiGO.

After logging in to Nexus with your 
[repository credentials](/tomtom-indigo/documentation/getting-started/introduction), you can find 
the SDK versions under `com/tomtom/indigo/tomtom-indigo-sdk`:

- Find the relevant version of the `tomtom-indigo-sdk`.
- Click on the `+` icon to the left of this version to open its folder.
- Click on the `tomtom-indigo-sdk-<version>.tar.gz` file to see its details in the panel on the
  right.
- Click the `Path` link in the panel on the right to start downloading the zip.

__Next step:__
[Accessing the Artifact repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).
