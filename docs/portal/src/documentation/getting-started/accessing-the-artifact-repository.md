---
title: Accessing the Artifact Repository
---

The [TomTom repository credentials](/tomtom-indigo/documentation/getting-started/introduction) that you
used in step 
[Installing TomTom IndiGO SDK](/tomtom-indigo/documentation/getting-started/installing-tomtom-indigo-sdk)
are also needed by the build process, to retrieve TomTom IndiGO platform dependencies from the IVI
[Nexus](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk)
repository, a binary repository hosted by TomTom.

These credentials can be passed to the build process in several ways:

- [Storing credentials globally](#storing-credentials-globally)
- [Specifying credentials on the command line](#specifying-credentials-on-the-command-line)

## Storing credentials globally

You can store the credentials in your `gradle.properties` file, in the directory specified by the
`GRADLE_USER_HOME` environment variable, usually `~/.gradle/`. When such a file does not exists yet,
you can create one.

__Windows:__ Use the `gradle.properties` file in the `%UserProfile%\.gradle` folder.

In your `gradle.properties` file, add the following:

```cmd
nexusUsername=<your-username>
nexusPassword=<your-password>
```

__Note:__ On some operating systems, a hash character `#` in the username or password must be
escaped as `\#` to be properly recognized.

## Specifying credentials on the command line

You can also add credentials with the following command:

__Linux or Mac:__
```cmd
./gradlew -PnexusUsername=<username> -PnexusPassword=<password> build
```

__Windows:__
```cmd
.\gradlew.bat -PnexusUsername=<username> -PnexusPassword=<password> build
```

__Next step:__
[Getting the TomTom API key](/tomtom-indigo/documentation/getting-started/getting-the-tomtom-api-key).
