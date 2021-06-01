# IndiGO Platform Example Application

## Introduction

This repository contains the source code of example application built on IndiGO platform.

### `buildSrc/`

Code used by the Gradle to sync the Gradle modules and build the app.

### `docs/`

The folder containing documentation, examples, and guides to create IndiGO components.

### `gradle/`

The Gradle wrapper used to sync the Gradle modules and build the app.

### `modules/`

The root directory for modules. All Gradle modules inside are within two levels of subdirectories.

* `modules/common/`
  Modules with shared code used by applications, frontends, and services.

* `modules/frontends/`
  Modules for IndiGO frontend plugins.

* `modules/products/`
  Modules for applications based on IndiGO Platform.

* `modules/serviceapis`
  Modules with API definitions of IVI services.

* `modules/services`
  Modules with implementations of IVI services.

## Building and running

In order to build the project using dependencies from the IVI Nexus repository, credentials need to
be provided to access this repository. This can be done in different ways:

### `Store credentials in ~/.gradle/gradle.properties`

```bash
nexusUsername=<username>
nexusPassword=<password>
```

And then build with:

```bash
./gradlew build
```

### `Specify credentials through the commandline`

```bash
./gradlew -PnexusUsername=<username> -PnexusPassword=<password> build
```

## Setup development environment

In order to run the example app, you need to take some additional steps, such
as creating an Android Automotive emulator. This is done with

```
./gradlew createDevelopmentEmulator
```

For the entire setup process, please consult the following page:
[docs/getting-started-linux.md](docs/getting-started-linux.md)

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.
