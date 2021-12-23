---
title: Releases
hideMenu: true
hideSubmenu: true
---

Release notes for the TomTom IndiGO platform early-access release.

## Download the Example Application

The latest Example Application can be downloaded from the TomTom IndiGO external binary repository, 
using the credentials you received after signing the Evaluation Agreement (EA). To request access, 
please go to the [Getting Started](/tomtom-indigo/documentation/getting-started/introduction) guide, and 
"Register for early access".

The latest source can be found in 
[this location](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk).
You need to expand a version and select the `tomtom-indigo-sdk-<version>.tar.gz` file and select 
"Path" in the right side frame, to start the download.

For example, the latest version, as of 17/12/2021, can be found here: 
[tomtom-indigo-sdk-1.0.2927-823.tar.gz](https://repo.tomtom.com/repository/ivi/com/tomtom/indigo/tomtom-indigo-sdk/1.0.2927-823/tomtom-indigo-sdk-1.0.2927-823.tar.gz).

The Example Application contains the code needed to get started developing for the TomTom IndiGO 
platform, and contains Gradle files pointing to the binary artifacts of the release.

The credentials are also used to access the binary repository when compiling the TomTom IndiGO 
application. More details can be found in the 
[Getting Started](/tomtom-indigo/documentation/getting-started/introduction) guide.

The first part of the version number `x.y.zzzz` refers to the TomTom IndiGO platform version the 
Example Application uses. The second part `-ppp` is the version of the example application release.

Once extracted, you will find these directories:

```cmd
examples/*.apk
examples/products_indigo_examples-<version>-sources
```

The APKs can be run directly on hardware or in an Android Automotive emulator.
Or you can build your own APK from the sources directory.

The README in the sources folder contains information related to the structure of the application.

## Getting started

The [Getting Started](/tomtom-indigo/documentation/getting-started/introduction) guide, contains
instructions on how to setup your development environment, how to run TomTom IndiGO on the emulator, 
and how to develop the application further.

## Examples documentation

Documentation related to the example source can be found in the 
[Tutorials and Examples](/tomtom-indigo/documentation/tutorials-and-examples/overview) section.

To get started with extending the TomTom IndiGO platform, please read:

- [Create a Frontend Plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
- [Create an IVI Service](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-an-ivi-service)

## API Reference documentation

The latest API reference documentation for the TomTom IndiGO platform, can be found 
[here](/tomtom-indigo/api-reference/introduction).
