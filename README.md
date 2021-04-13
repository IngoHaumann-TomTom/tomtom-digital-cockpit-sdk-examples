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

## Build

Build the application with plugins:

```bash
./gradlew build
```

Install the application on all available devices:

```bash
./gradlew installDebug
```

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.
