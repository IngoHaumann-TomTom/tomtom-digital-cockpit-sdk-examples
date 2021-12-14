---
title: Integrating IndiGO
---

When IndiGO is running in a car it can potentially communicate with many subsystems and sensors 
in the car. This can be to provide status of various inputs, for example which doors and windows 
are open or closed, or to be able to set a setting from the UI, for example setting the current 
driving mode in the car. 

This requires integration with the hardware, and IndiGO is built on top of Android Automotive OS, 
which provides a hardware abstraction layer (HAL) that provides a consistent interface to the many 
different physical transport layers in the car.

Read more about vehicle integration of IndiGO in the 
[Integrating in Vehicle](/indigo/documentation/integrating-indigo/integrating-in-vehicle) section.

For IndiGO to obtain the correct permissions in the Android platform, it needs to run as a system 
application, and signed with the platform key. Read more about how to do this and the hardware 
requirements in the 
[Installing IndiGO on Hardware](/indigo/documentation/integrating-indigo/installing-indigo-on-hardware) 
section.

When developing the IndiGO platform, we have used a Samsung Galaxy Tab S5e as reference hardware, 
we also provide a reference image for this, based on LineageOS, which in turn is based on Android 
Automotive. Read how to obtain this image and flash the hardware in the 
[Flashing the Reference Hardware](/indigo/documentation/integrating-indigo/flashing-the-reference-hardware) 
section.

IndiGO also comes with support for a companion application to extend the IndiGO platform 
experience with functionality from the user's mobile phone. Read more about the 
[Companion Application](indigo/documentation/integrating-indigo/companion-application) and how to 
connect to the IndiGO product.

