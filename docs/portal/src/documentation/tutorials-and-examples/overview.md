---
title: Tutorials and Examples
layout: default
---

This is a list of tutorials and how-to guides on topics that you may come across while developing 
for the TomTom IndiGO platform. Each how-to guide explains step-by-step how you can extend 
TomTom IndiGO.


## Example applications

The example applications are provided as sample implementations. The applications allow you to 
verify your build environment by making sure Gradle can build this TomTom IndiGO example APK and 
install it on an emulator. The applications also contain several examples on how to add 
functionality to your product. All the examples can be found under the `examples/` folder. Under 
the `template/` folder you can find an empty example application. This only contains the default 
IndiGO platform functionality, without any additional example source, which can be used as a base 
for your own product.

Check out the functionality of the TomTom IndiGO platform in the 
[walk-through](/tomtom-indigo/documentation/platform-overview/example-apps) of an example 
application, or get it yourself by following our 
[getting started](/tomtom-indigo/documentation/getting-started/introduction)
guide.

Additional functionality is described in the tutorials and how-to guides below.

## Setup

- [Integrate TomTom IndiGO into a Gradle project](/tomtom-indigo/documentation/tutorials-and-examples/setup/integrate-tomtom-indigo-into-a-gradle-project)

  This guide explains how to add TomTom IndiGO to a new or already existing Gradle project.

- [Configure a TomTom API key](/tomtom-indigo/documentation/tutorials-and-examples/setup/configure-a-tomtom-api-key)

  This guide explains how to enable TomTom IndiGO's map and navigation-related features, using your 
  TomTom API key.

## Basics

- [Create a frontend plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)

  This guide explains how to add a frontend with panels, how to link your frontend to a `Fragment`,
  and how to make a menu item for it.

- [Create an IVI service](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-an-ivi-service)

  This guide explains how to define the interface of your service, how to implement and deploy it,
  and how to use it from the client side.

- [Configure debug tabs](/tomtom-indigo/documentation/tutorials-and-examples/basics/configure-debug-tabs)

  This guide explains how to create a new tab for the debug menu, and how to customize the existing
  tabs.

## Media

- [Create a new media user interface](/tomtom-indigo/documentation/tutorials-and-examples/media/create-a-new-media-user-interface)

  This guide explains how to add a single panel user interface for a radio source.

- [Customize a media source](/tomtom-indigo/documentation/tutorials-and-examples/media/customize-a-media-source)

  This guide explains how to add a new media source that will automatically show up as an additional
  choice in the media panel.

## Communications

- [Create a custom connection service](/tomtom-indigo/documentation/tutorials-and-examples/communications/create-a-custom-connection-service)

  This guide explains how to add a new connection service to make phone calls via TomTom IndiGO, 
  like Voice Over IP (VOIP).

- [Create a custom contacts service](/tomtom-indigo/documentation/tutorials-and-examples/communications/create-a-custom-contacts-service)

  This guide explains how to add a contacts service that provides information that TomTom IndiGO can
  display in the contacts panel.

- [Create a custom recent calls service](/tomtom-indigo/documentation/tutorials-and-examples/communications/create-a-custom-recentcalls-service)

  This guide explains how to add a recent calls service that provides information that TomTom 
  IndiGO can display in the recent calls panel.

## Customization

- [Use the configuration framework](/tomtom-indigo/documentation/tutorials-and-examples/customization/use-the-configuration-framework)

  This guide explains how to create static and dynamic configurations by using the configuration
  generator, and how to use these to customize the application.
  
## Deployment

- [Configure the runtime deployment of the IVI System](/tomtom-indigo/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system)

  This guide explains how to run services in a separate process, configure multiple TomTom IndiGO
  instances, and how to deploy Android services in the same process as an IVI service.
