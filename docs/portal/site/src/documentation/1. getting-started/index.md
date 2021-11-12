---
title: Getting Started
layout: default
---

Creating an IndiGO product requires two parts: development and vehicle integration. We provide
information on both parts here on this developer portal. You can use the site map on the left to
skip ahead, or search for specific content by using the search bar above; just click the magnifying
glass and enter your query.

## Building for IndiGO

Starting on a new project with an unfamiliar code base can be a daunting task. This documentation
will explain the necessary steps to set up your environment properly, where and how to get the
required keys and dependencies, and where to find example implementations and how-to guides for
several types of plugins.

When you've completed the [getting started](#getting-started) guide, you can have a look at the
[example application](#example-application) or perhaps already follow along with a
[how-to](#how-to-guides) to add a first plugin of your own.

### Getting started

Before you can start development with the IndiGO platform, you'll have to make sure your system is
set up correctly. The 
[getting started guide](/indigo/documentation/getting-started/getting-started-guide) will help you
set up an environment in which IndiGO development is possible.

In separate steps it will tell you which JDK is required, how to obtain a TomTom NavKit2 API key to
be able to use maps and navigation features, how to set up an Android emulator, and configure your
Android Studio.

### Developing

When you've set up your development environment, you are ready to start developing an IndiGO
application.

Make sure you've familiarized yourself with the [architecture and design of
IndiGO](/indigo/documentation/developing/). Understanding this will make it easier to grasp the
IndiGO concepts of plugins, UI components, and more.

IndiGO has a [plugin](/indigo/documentation/developing/plugins) architecture so you can easily add
your functionality, or remove what you don't need. [Services](/indigo/documentation/developing/)
provide data to frontends, both of which are also plugins. The [system
UI](/indigo/documentation/developing/system-ui) and [UI
components](/indigo/documentation/developing/ui-components) are defined in such a way that they can
be [themed](/indigo/documentation/developing/theming-and-customization) easily.

To better understand the relation between the various plugins within a functional domain, we provide
some [high level overviews](/indigo/documentation/developing/platform-domains/) of these areas. For
example: [navigation](/indigo/documentation/developing/platform-domains/navigation),
[media](/indigo/documentation/developing/platform-domains/media),
[communications](/indigo/documentation/developing/platform-domains/communications),
[configuration](/indigo/documentation/developing/platform-domains/configuration-framework),
[settings](/indigo/documentation/developing/platform-domains/settings-framework), [vehicle
functions](/indigo/documentation/developing/platform-domains/vehicle-functions), and [Voice Personal
Assistants (VPA)](/indigo/documentation/developing/platform-domains/voice-personal-assistant-vpa).

### Example application

The [example application](/indigo/documentation/tutorials-and-examples/example-app) repository is 
provided as
a sample implementation. The application allows you to verify your build environment by making sure
Gradle can build this IndiGO example APK and install it on an emulator.

The example application also contains several examples on how to add functionality to your product.

__Note:__ All these examples are currently part of a single application. A future release will split
the example app repository into a collection of applications, each demonstrating only a single
functionality.

### How-to guides

When you're familiar with the IndiGO architecture and the way it names and uses plugins and
components, you can start following one of the
[how-to guides](/indigo/documentation/tutorials-and-examples/how-to-guides). 
A how-to guide will lead you step by step
through the process of adding functionality to your product.

There are how-to guides on basics like adding a
[frontend](/indigo/documentation/tutorials-and-examples/how-to-guides/how-to-create-a-frontend-plugin)
or a
[service](/indigo/documentation/tutorials-and-examples/how-to-guides/how-to-create-an-ivi-service),
up to more advanced topics like [installing on your own
hardware](/indigo/documentation/tutorials-and-examples/how-to-guides/how-to-install-on-hardware) or
[adding custom actions to a media
source](/indigo/documentation/tutorials-and-examples/how-to-guides/how-to-customize-a-media-source).

## Integrating IndiGO in a vehicle

__Note:__ This documentation is not finished yet.

[TODO(IVI-4769)]:# (Create Vehicle Integration overview page)

## API reference

Developing for IndiGO requires a set of Application Programming Interfaces (API): the main IndiGO
API and the TomTom Android Tools API. The reference documentation for both can be found on the
[API reference documentation page](/indigo/api-reference). It provides the details of all classes 
and functions, and insights into the relation between all modules and packages.

## IndiGO releases

All IndiGO releases are listed [here](/indigo/releases).
