---
title: Development
---

In this IndiGO development guide, you can find a set of documents that describe the fundamentals 
of the IndiGO platform, and the building blocks that are used to build and create new 
functionality.

The IndiGO platform is a framework built on top of 
[Android Automotive](https://source.android.com/devices/automotive) OS, that allows you to 
create functionality needed for digital cockpits. It comes with a set of ready-made applications, 
and a set of platform APIs that extend the functionality of the standard Android Automotive 
platform.

The IndiGO product is deployed as one single Android APK, which means that each _application_ for 
IndiGO is built as a set of plugins to the IndiGO framework. What is deployed as part of the end 
product, is configured at build time. You can, for example, select which standard (or _stock_) 
IndiGO applications should be part of your product, and which should be replaced with your own 
_applications_, or merely add new functionality.

The User Interface (UI) part of an IndiGO _application_ is built using 
[frontend plugins](/indigo/documentation/development/frontend-plugins). These are created using 
standard Android layouts and fragments, together with _panels_, which are Android views made to 
fit in with the IndiGO UI framework, each with its own specialized functionality. IndiGO also 
comes with a set of UI components, that are based on the standard Android components, but tailored 
for the IndiGO platform and its look and feel. You can read more about this in the
[UI components](/indigo/documentation/development/ui-components)
section.

The [System UI](/indigo/documentation/development/system-ui) section explains how _panels_ are 
arranged, managed and presented to the user on the screen.

Non UI related logic can be wrapped in an 
[IVI service](/indigo/documentation/development/ivi-services), which provides a mechanism to 
encapsulate longer-running tasks and business logic for some distinct functionality in the 
platform. These are based on Android 
[services](https://developer.android.com/guide/components/services), and use the same mechanisms, 
but hide some of the details of the Android service implementation. 

The look and feel of the IndiGO product can be changed through 
[Theming and Customization](/indigo/documentation/development/theming-and-customization), which is 
based on Android's 
[styles and themes](https://developer.android.com/guide/topics/ui/look-and-feel/themes), with 
additional functionality to support different themes and swapping them at runtime.

The IndiGO platform is divided into different functional 
[platform domains](/indigo/documentation/development/platform-domains/overview), 
which are described separately.
