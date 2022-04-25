---
title: Accessing TomTom IndiGO SDK Example Sources
---

The example sources that come with the TomTom IndiGO platform contain the source code that goes 
with the tutorials, to get you started with developing for TomTom IndiGO. The applications also 
allow you to verify your build environment by making sure Gradle can build a TomTom IndiGO example 
APK and install it on an emulator.

__Note:__ The TomTom IndiGO SDK will soon be delivered through GitHub instead of Nexus. If you have
received the GitHub credentials, please follow the steps described for
[accessing the TomTom IndiGO SDK through GitHub](#accessing-the-tomtom-indigo-sdk-example-sources-through-github).
Otherwise, please follow the instructions below.

## Accessing the TomTom IndiGO SDK Example Sources through Nexus

When you provided the 
[signed Evaluation Agreement](/tomtom-indigo/documentation/getting-started/introduction), you 
would have received the credentials to access our TomTom IndiGO Nexus repository.

The TomTom IndiGO SDK Example Sources and APKs can be found on 
[Nexus](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk).

After logging in to Nexus with your 
[repository credentials](/tomtom-indigo/documentation/getting-started/introduction), you can find 
the SDK versions under `com/tomtom/indigo/tomtom-indigo-sdk`:

- Find the relevant version of the `tomtom-indigo-sdk`.
- Click on the `+` icon to the left of this version to open its folder.
- Click on the `tomtom-indigo-sdk-<version>.tar.gz` file to see its details in the panel on the
  right.
- Click the `Path` link in the panel on the right to start downloading the zip.

## Accessing the TomTom IndiGO SDK Example Sources through GitHub

The latest example sources for the TomTom IndiGO platform can be downloaded from GitHub:
https://github.com/tomtom-internal/tomtom-indigo-sdk-examples

Run the following command to clone the sources:

```cmd
git clone git@github.com:tomtom-internal/tomtom-indigo-sdk-examples.git
```

To see the latest version of the sources, check out the `main` branch:

```cmd
git checkout main
```

The source is tagged for each release of the SDK. To see the source specific to a version, run the 
following command:

```cmd
git checkout tags/<sdk-version>
```

__Next step:__
[Accessing the Artifact repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).
