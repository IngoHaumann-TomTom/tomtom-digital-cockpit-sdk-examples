---
title: Documentation
---

Creating a TomTom IndiGO product requires two distinct activities: development of the software using 
the TomTom IndiGO SDK, and integrating that software in the vehicle. Development for TomTom IndiGO 
is an independent activity, and can be achieved using emulators and mobile devices. We provide 
information on both parts here on this developer portal. You can use the site map on the left to 
skip ahead, or search for specific content by using the search bar above; just click the magnifying 
glass and enter your query.

## Introducing the TomTom IndiGO platform

A good start is the
[TomTom IndiGO platform overview](/indigo/documentation/platform-overview/what-is-indigo) section 
to find out more about TomTom IndiGO, the concepts and the building blocks that make up the 
platform.

## Building for TomTom IndiGO

Starting on a new project with an unfamiliar code base can be a daunting task. This documentation
will explain the necessary steps to set up your environment properly, where and how to get the
required keys and dependencies, and where to find example implementations and tutorials for
several types of plugins.

When you've completed the [getting started](#getting-started) guide, you can have a look at the
[example application](#example-application) or perhaps already follow along with a
[tutorials](#tutorials) to add a first plugin of your own.

### Getting started

Before you can start development with the TomTom IndiGO platform, you'll have to make sure your 
system is set up correctly. The 
[getting started guide](/indigo/documentation/getting-started/introduction) will help you set up an
environment in which TomTom IndiGO development is possible.

In separate steps it will tell you which JDK is required, how to obtain a TomTom NavKit2 API key to
be able to use maps and navigation features, how to set up an Android emulator, and configure your
Android Studio.

### Developing

When you've set up your development environment, you are ready to start developing a TomTom IndiGO
application.

Make sure you've familiarized yourself with the 
[architecture and design of TomTom IndiGO](/indigo/documentation/development/introduction). 
Understanding this will make it easier to grasp the TomTom IndiGO concepts of plugins, 
UI components, and more.

TomTom IndiGO has a plugin architecture so you can easily add your functionality, or remove what 
you don't need. 
[Services](/indigo/documentation/development/ivi-services/) provide data to 
[frontends](/indigo/documentation/development/frontend-plugins), both of which are also plugins. 
The [system UI](/indigo/documentation/development/system-ui) and 
[UI components](/indigo/documentation/development/ui-components) are defined in such a way that 
they can be [themed](/indigo/documentation/development/theming-and-customization) easily.

To better understand the relation between the various plugins within a functional domain, we 
provide some [high level overviews](/indigo/documentation/development/platform-domains/) of these 
areas. This includes: 
[navigation](/indigo/documentation/development/platform-domains/navigation), 
[media](/indigo/documentation/development/platform-domains/media), 
[vehicle functions](/indigo/documentation/development/platform-domains/vehicle-functions), 
[communications](/indigo/documentation/development/platform-domains/communications), 
[Voice Personal Assistants (VPA)](/indigo/documentation/development/platform-domains/voice-personal-assistant-vpa), 
[configuration](/indigo/documentation/development/platform-domains/configuration-framework), and 
[settings](/indigo/documentation/development/platform-domains/settings-framework).

### Example application

The example application is provided as a sample implementation. The application allows you to 
verify your build environment by making sure Gradle can build this TomTom IndiGO example APK and 
install it on an emulator. The example application also contains several examples on how to add 
functionality to your product.

Check out the functionality of the TomTom IndiGO platform in the 
[walk-through](/indigo/documentation/platform-overview/example-app) of the example application, 
or get it yourself by following our [getting started](/indigo/documentation/getting-started/introduction) guide.

Additional functionality is described in the [tutorials](#tutorials) section.

__Note:__ All the examples are currently part of a single application. A future release will 
split the example app repository into a collection of applications, each demonstrating only a 
single functionality.

### Tutorials

When you're familiar with the TomTom IndiGO architecture and the way it names and uses plugins and
components, you can start following one of the
[tutorials](/indigo/documentation/tutorials-and-examples/overview). 
A tutorial will lead you step by step through the process of adding functionality to your product.

There are tutorials on basics like adding a
[frontend](/indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
or a
[service](/indigo/documentation/tutorials-and-examples/basics/create-an-ivi-service),
up to more advanced topics like 
[adding custom actions to a media source](/indigo/documentation/tutorials-and-examples/media/customize-a-media-source).

## Integrating TomTom IndiGO

Read more about [integrating TomTom IndiGO](/indigo/documentation/integrating-indigo/introduction) in a
vehicle or on other hardware.

## API reference

Developing for TomTom IndiGO requires a set of Application Programming Interfaces (API): the main 
TomTom IndiGO API and the TomTom Android Tools API. The reference documentation for both can be 
found on the[API reference documentation page](/indigo/api-reference/introduction). It provides the 
details of all classes and functions, and insights into the relation between all modules and 
packages.

## TomTom IndiGO releases

All TomTom IndiGO releases are listed [here](/indigo/releases/introduction).

__Get started:__ [Getting Started](/indigo/documentation/getting-started/introduction)

