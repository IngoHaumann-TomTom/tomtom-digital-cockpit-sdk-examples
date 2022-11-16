---
title: Splash Screen
---

The splash screen is shown when a user starts up TomTom Digital Cockpit and the required frontend 
components are not yet ready. When the frontend components are ready, the splash screen is dismissed 
and the home screen is displayed.

## Dismissal

The splash screen is only dismissed when all the required frontend components are ready. You can 
also specify additional components that are required to load before the splash screen is dismissed.

## Passenger screens

Passengers will see the same splash screen that the driver does.

## Customization

The splash screen can be customized to only be displayed when different frontend components are 
ready. The logo and background color are not currently customizable, however the splash screen can 
easily be replaced with any Android view.

| Component     | Customizable  |
| ------------- | ------------- |
| Dismissal | The splash screen is dismissed when the system UI, main menu, and all necessary frontend components are ready. The dismissal time can be extended to include other frontend components. |
| Replaceable | The splash screen can be replaced with any Android view. |

## Levels of customization

The splash screen can be replaced with any custom Android view.

<ImageArticleGrid articles={
 [
   {
     title: 'Out of the box',
     body: 'What the splash screen looks like by default.',
     img: {
       src: 'https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit/image-components/system-ui/splash-screen/out-of-box.png',
       alt: 'out of box',
     }
   },
  {
     title: 'Replaced',
     body: 'The splash screen can be replaced with any custom Android view..',
     img: {
       src: 'https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit/image-components/system-ui/splash-screen/replaced.png',
       alt: 'themed',
     }
   }
 ]}
/>