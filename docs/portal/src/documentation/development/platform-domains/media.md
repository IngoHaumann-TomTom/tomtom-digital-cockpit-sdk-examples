---
title: Media
layout: default
---

Media plays an important part in users’ digital life and is the most used function of infotainment
systems. This encompasses radio, music, podcasts, and audio books. TomTom IndiGO provides a way to
browse and stream content from any audio source.

Media sources (or sources) are apps that provide media content. Sources provide content in a
hierarchy. For example, an album contains tracks, and an artist overview may contain all albums.
The source determines how it organizes its content.

This document explains the structure of the media domain through the following major topics:

- [High-level overview](#high-level-overview)
    - [Android Automotive media ecosystem](#android-automotive-media-ecosystem)
    - [Division of responsibility](#division-of-responsibility)
    - [Available off-the-shelf (or stock) components](#available-off-the-shelf-or-stock-components)
- [Detailed overview](#detailed-overview)
    - [Configuring the media application frontend](#configuring-the-media-application-frontend)
    - [Media library](#media-library)
    - [Media sources library](#media-sources-library)
        - [Panels](#panels)
        - [Media controls](#media-controls)
        - [Policies](#policies)

## High-level overview

The TomTom IndiGO Application Suite provides flexible architecture components to allow a wide range
of media sources to be integrated, and to provide the end-user with a consistent user interface
through which they can browse and play media.

The following diagram shows a high-level overview of the architecture of media in TomTom IndiGO. It
displays the following components:
- The media application frontend. This is the user interface.
- The media service interface. This interface defines how a frontend (or potentially another
  service) can interact with the media application service.
- The media application service. This is the service that implements the media service interface.
- The `MediaBrowser`. This is the connection to the
  [Android Automotive media ecosystem](#android-automotive-media-ecosystem).

![Media high-level overview image](images/media_domain-high-level-overview.svg)

### Android Automotive media ecosystem

TomTom IndiGO hooks into the existing Android Automotive media ecosystem. All developers developing
a media app can develop their app in this standardized way. Many media apps on the market today,
made for Android Automotive OS, already conform to this standard. Because of
that, basic support for these apps is present in TomTom IndiGO. Advanced support can be added by
[configuring the media application frontend](#configuring-the-media-application-frontend).

The Android Automotive media standard is well documented. To familiarize with this topic, the
[Build media apps for cars](https://developer.android.com/training/cars/media) and
[Add Android Automotive OS support to your media app](https://developer.android.com/training/cars/media/automotive-os)
docs from Android are available.

### Division of responsibility

The [high-level overview](#high-level-overview) shows the application frontend (the user interface),
the media service interface, and an application service.

To understand how responsibilities are divided between them, the first component to consider is
the media service interface. Its functions can be divided into three categories:
- The currently available sources.
- The current status. For example:
    - What is currently playing?
    - Which source is playing it?
- Actions. These are commands that can be sent to a source. For example: pause playback. The
  service signals which actions are currently available, and has the ability to send (or launch)
  them.

The service offers no way to browse media content. This is done in the frontend itself for
performance reasons: sending content via inter-process communication between service and frontend
is slow. As a result, both the frontend and the service use the Android Automotive media API.

The frontend displays the current media state as offered by the application service, and allows
the browsing of media content for all installed media sources. It offers a list of available
sources, and when one is selected, allows the user to browse through its content hierarchy, and to
start and control media playback.

### Available off-the-shelf (or stock) components

By default, the TomTom IndiGO platform offers a delightful user interface that provides a browsable
hierarchy of media content which can be played and/or browsed from installed media sources.

This user interface can be customized, expanded upon, and even replaced, to accommodate different
types of media sources (for example: radio, video playing) while ensuring that the visual style
holds up to the highest standards for user experience simplicity and distraction-free usage.

The off-the-shelf application frontend, the stock frontend, can be configured using
[`MediaPolicyFrontendExtension`](TTIVI_PLATFORM_API)s to activate a
[`PolicyProvider`](TTIVI_PLATFORM_API) which configures a specific source. See
[configuring media frontend](#configuring-the-media-application-frontend) for more details.

If that is not sufficient, it is possible to completely replace the stock frontend and/or service
with custom implementations. Similar to configuring the stock media frontend, outright replacement
of the stock media frontend is done via a [`FrontendExtension`](TTIVI_PLATFORM_API). Check out the
[How to create a frontend plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
page and the documentation for [`FrontendExtension`](TTIVI_PLATFORM_API) for more details on how to
replace a stock frontend.

## Detailed overview

Let's take a look at the various components in the media domain, and how they connect and interact.

![Media detailed overview image](images/media_domain-detailed-overview.svg)

This diagram shows the most relevant components:
- The media service, see [division of responsibility](#division-of-responsibility).
- The source client, which is contained in the [media library](#media-library).
- The [`PolicyProvider`](TTIVI_PLATFORM_API), which provides [policies](#policies). For example, the
  [`UserFlowPolicy`](TTIVI_PLATFORM_API) can be used to customize user flows such as browsing a
  source, logging in and opening settings.

### Configuring the media application frontend

- To simply customize how the content is displayed on screen, [policies](#policies) can be used.
- Adding support for a custom media action provided by a source (for example, a "like" or an "add to
  playlist" functionality) is easy, by creating a new [media control](#media-controls).
- For more complex use cases, it is possible to create [custom panels](#panels) to show an entirely
  different user interface for a source.

These configurations are activated via a frontend plugin using Gradle configuration. The
[How to create a frontend plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
guide explains this procedure.

Some customization of the frontend can be achieved using the
[media plugins configuration](#media-plugins-configuration), based on the
[configuration-framework](/tomtom-indigo/documentation/tutorials-and-examples/customization/use-the-configuration-framework).

### Media plugins configuration

The media plugin default configuration can be changed by adding a custom configuration resource file
in your application, such as `<module>/res/value/ttivi-media-configuration.xml`. The custom
configuration overrides the default values. The
[How to configure the media plugin](/tomtom-indigo/documentation/tutorials-and-examples/media/configure-the-media-plugins)
guide explains this procedure.

### Media library

The [`appsuite_media_api_common_core`](TTIVI_PLATFORM_API) library can be used to retrieve media
content for a custom implementation.

### Media sources library

The [`appsuite_media_api_common_frontend`](TTIVI_PLATFORM_API) library contains the building blocks
for a more thorough customization of media frontends.

#### Panels

The Android Media API standardizes support for all sorts of media content, such as radio, which
might not be suitable for the standard user interface offered by the stock media application
frontend.

To ensure the most straightforward development of support for such alternative sources, a number
of facilities are provided to immediately start building suitable user interfaces.
The [`appsuite_media_api_common_frontend`](TTIVI_PLATFORM_API) library contains these facilities.

#### Media controls

A media source may offer custom actions. Custom actions add unique capabilities
specific to one source, such as liking a song. These actions may need to be integrated into the
user interface so that the end-user can access them.

Creating a media control can be the way to do that. A media control is a visual control for a
media-related command. They appear in the following locations:
- The playback controls. This is the bottom bar in the stock media frontend.
- The mini player. This is the main process panel from the stock media frontend that appears when the
  frontend is closed.

Further information is available in the [`appsuite_media_api_common_frontend`](TTIVI_PLATFORM_API)
library, and more specifically, in the [`MediaControl`](TTIVI_PLATFORM_API) page.

#### Policies

When integrating a new media source into a product, the media content it offers may be a challenge
to display properly to the user: even if the Android standard is quite well known and documented,
there are still some small areas where inconsistencies can occur, and policies offer a way to level
those out.

Policies are the simplest way to customize the content offered by a media source, so that the
stock Media user interface can display it in the best way; for example by modifying media titles
when they're given as a single "artist - track name" string, or by changing how a specific type of
media is displayed.

Further information is available in the [`appsuite_media_api_common_frontend`](TTIVI_PLATFORM_API)
library, and more specifically in the [`PolicyProvider`](TTIVI_PLATFORM_API) page.
