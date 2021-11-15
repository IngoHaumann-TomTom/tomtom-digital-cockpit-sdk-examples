---
title: Platform Domains
layout: default
---

The IndiGO platform is divided into a number of functional domains which are summarized here.

## Navigation

Navigation is central to the IndiGO experience. It is built on top of TomTom's online navigation
SDK: [NavKit2](https://developer.tomtom.com/docs-and-tools). When the connection is lost during
turn-by-turn guidance or free driving, the system will keep operating normally using cached map
content. Online navigation enables fresh and always up-to-date map content.
[Navigation](/indigo/documentation/development/platform-domains/navigation)

## Media

Entertainment plays an important part in users’ digital life and is the most used function of
infotainment systems. IndiGO provides a way for the user to browse and stream any audio source
including media sources such as radio stations, music streaming services, podcasts and audio books.
[Media](/indigo/documentation/development/platform-domains/media)

## Vehicle Functions

IndiGO can show the state of the vehicle and allows users to interact with it. Whether it is
controlling the heating, ventilation, and air-conditioning (HVAC) of the vehicle, seeing the
temperature of the oil, or opening a window, IndiGO provides the means to give drivers full control
over their vehicle.
[Vehicle Functions](/indigo/documentation/development/platform-domains/vehicle-functions)

## Communications

Users expect communication, instant messaging, media consumption, social networking, and
location-based services to be usable anywhere and anytime. IndiGO provides a seamless connection to
the users’ digital life, whether it is a phone call or replying to instant messages that arrive
while driving.
[Communications](/indigo/documentation/development/platform-domains/communications)
 
## Voice Personal Assistant (VPA)

IndiGO allows the integration of 3rd-party voice engines to support voice personal assistants
(VPAs) in the vehicle. A generalized interface enables the customer to speak commands and repl
to system queries by voice. For example, the user can ask the system to make a phone call or find
a nearby hospital by talking.
[Voice Personal Assistant (VPA)](/indigo/documentation/development/platform-domains/voice-personal-assistant-vpa)

## Configuration Framework

Branding is important, which is why IndiGO provides the means to customize its look. Colors,
margins, sizes, and fonts can all be changed by applying a different theme. IndiGO also supplies a
set of UI components that share a look-and-feel and adhere to the theme, such that the user
can have a seamless UI experience.
[Configuration Framework](/indigo/documentation/development/platform-domains/configuration-framework)

## Settings Framework

It may be important to remember some of the state of your service or frontend. The user expects some
continuity when relaunching an app, and a consistent state between road trips. IndiGO facilitates
that by providing a settings service where you can store and retrieve your setting values in a
type-safe manner.
[Settings Framework](/indigo/documentation/development/platform-domains/settings-framework)
