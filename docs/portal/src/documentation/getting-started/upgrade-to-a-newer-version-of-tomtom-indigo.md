---
title: Upgrade to a Newer Version of TomTom IndiGO
---

While developing with TomTom IndiGO, you may wish to integrate new features and improvements of
TomTom IndiGO. In other words, you want to upgrade to a newer version of the TomTom IndiGO
platform.

## Upgrade to a Newer Version of the TomTom IndiGO Platform

To have your application use a newer version of the TomTom IndiGO platform, you should edit the
[`/build-logic/libraries.versions.toml`](https://github.com/tomtom-international/tomtom-indigo-sdk-examples/blob/main/build-logic/libraries.versions.toml#L17)
file and search for a line like:

```toml
indigoPlatform = "1.0.3714"
```

Change the version to a recent version listed in the
[Releases](/tomtom-indigo/releases/releases#releases)
section of the TomTom Developer Portal.

When rebuilding the application, Gradle will pull the specified version of the TomTom IndiGO
binaries from the TomTom repository and use these in the build process. This may lead to warnings in
the build process, when elements of the TomTom IndiGO API have become deprecated. You should adapt
your application to solve these warnings.

## Upgrade to a Newer Version of the Platform and Example Apps

When you want to take advantage of larger changes in the platform, like a new feature or a new
module, you may need a newer version of the template app or example apps. In such a situation you
should get the new version of the TomTom IndiGO SDK by
[pulling it from GitHub](/tomtom-indigo/documentation/getting-started/accessing-tomtom-indigo-sdk-example-sources#accessing-the-tomtom-indigo-sdk-example-sources-through-github)
and then merging it with your application.

When rebuilding the application, the updated build files make Gradle pull the new version of the
TomTom IndiGO binaries from the TomTom repository, and use these in the build process.
