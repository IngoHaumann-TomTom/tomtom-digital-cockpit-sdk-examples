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

## Setting up the development environment

For the entire setup process, please consult the following page:
[docs/portal/getting-started.md](docs/portal/getting-started.md)

## Copyright

Copyright © 2020 TomTom NV. All rights reserved.

This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
used for internal evaluation purposes or commercial use strictly subject to separate
license agreement between you and TomTom NV. If you are the licensee, you are only permitted
to use this software in accordance with the terms of your license agreement. If you are
not the licensee, you are not authorized to use this software in any manner and should
immediately return or destroy it.
