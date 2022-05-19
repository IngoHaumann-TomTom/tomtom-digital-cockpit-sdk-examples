---
title: Releases
hideMenu: true
hideSubmenu: true
---

Here you'll find all the TomTom IndiGO platform early-access releases.

## Download the TomTom IndiGO SDK

The latest TomTom IndiGO SDK can be downloaded from the TomTom IndiGO repository, using the
credentials you received after signing the Evaluation Agreement. To request access, please go to the
[Getting Started](/tomtom-indigo/documentation/getting-started/introduction) guide, and click
[Register for early access](/tomtom-indigo/request-access).

The TomTom IndiGO SDK contains the code needed to start developing for the TomTom IndiGO platform.
It contains the [template application](/tomtom-indigo/documentation/platform-overview/example-apps)
that show-cases all the off-the-shelf features of the IndiGO platform. It also contains a set of
[example applications](/tomtom-indigo/documentation/platform-overview/example-apps#example-apps)
demonstrating how to implement your own features with the TomTom IndiGO platform.

### TomTom IndiGO binary artefacts

The template and example applications come with Gradle files to build them. The build process needs
binary artefacts of the TomTom IndiGO platform, which are pulled from the TomTom binary artefact
repository. This requires the same credentials that you used when downloading the SDK.
For more details, see
[accessing-the-artifact-repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).

### TomTom IndiGO SDK version number

The TomTom IndiGO SDK version number has the format `x.y.zzzz-pppp`, where `x.y.zzzz` refers to the
TomTom IndiGO platform version and `-pppp` is the version of the template and example applications.

### TomTom IndiGO SDK content

Once extracted, you will find the following files and directory:

```cmd
examples/products_indigo_examples-<version>-*.apk
examples/products_indigo_examples-<version>-sources/
```

The APK files contain the IndiGO Template Application and can be run directly on hardware or in an
Android Automotive emulator. You can also opt to build your own APK from the sources directory.

The `README.md` file in the sources folder contains information related to the structure of the
application.

## Releases

TTIVI_RELEASES_ANCHOR
