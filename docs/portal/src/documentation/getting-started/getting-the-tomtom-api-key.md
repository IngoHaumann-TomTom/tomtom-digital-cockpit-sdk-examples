---
title: Getting the TomTom API Key
---

The TomTom IndiGO SDK uses TomTom's online NavKit2 services, for which an API key is required. An
evaluation key with limited authorization can be
[acquired immediately](#registering-as-a-tomtom-developer).
After signing TomTom's evaluation agreement, additional authorizations will be granted and you can
experience the full functionality of the TomTom IndiGO SDK and NavKit2.

__Note:__ When you do not intend to use the NavKit2 services, there is no need to set an API key.
However, the build process fails when no API key has been configured. You can set a Gradle property
that allows building without the API key, but TomTom IndiGO will not show the map and cannot 
calculate a route. Locate the Gradle properties file, as explained in the [Installing the API
key](#installing-the-api-key) section, and add the following line. You can then skip the rest of
this page.

```cmd
disableNavkit2ApiKeyBuildTimeCheck=true
```

## NavKit2

TomTom NavKit2 is the core technology that powers our complete navigation offering, including maps,
connected services and user interface (UI). For more information on NavKit2, see the
[NavKit2 product pages](https://www.tomtom.com/products/navkit).

## Registering as a TomTom Developer

- Navigate to [developer.tomtom.com](https://developer.tomtom.com/), click `Register`, and fill out
  the registration form.
- You will receive a confirmation email; click `Activate Account` and go to your
  [TomTom Developer dashboard](https://developer.tomtom.com/user/me/apps).
- The API key you get assigned should be used in the TomTom IndiGO SDK, but it must still be 
  authorized by TomTom for the NavKit2 features used by TomTom IndiGO. See also section
  [Evaluation agreement](#evaluation-agreement).

![My first API key](images/tomtom_my_first_api_key.png)

## Installing the API key

Now that you received your TomTom API key, it can be used with the TomTom IndiGO SDK.  Add it to 
either the top-level `local.properties` file or your `gradle.properties` file.

- The `local.properties` file can be created in the top-level folder of your local TomTom IndiGO SDK.
- The `gradle.properties` file is located in the folder specified by the `GRADLE_USER_HOME`
  environment variable, usually `~/.gradle/`. See
  [Configure a TomTom API Key](/tomtom-indigo/documentation/tutorials-and-examples/setup/configure-a-tomtom-api-key)
  for details and alternative ways to configure the key.
    - __Windows:__ Use the `%UserProfile%\.gradle` folder to find or create the 
    `gradle.properties` file.

Add a line to have the Gradle property match your TomTom API key:

```cmd
navkit2ApiKey=<your-tomtom-api-key>
```

## Evaluation agreement

The initial API key has restrictions on the functionality and the amount of requests you can do.
To enjoy the full functionality,
[apply for an evaluation agreement](/tomtom-indigo/documentation/getting-started/introduction) with TomTom.

__Note:__ You will not get a new API key, but TomTom will grant access to the navigation services
for your API key.

__Next step:__
[Configuring Android Studio](/tomtom-indigo/documentation/getting-started/configuring-android-studio).
