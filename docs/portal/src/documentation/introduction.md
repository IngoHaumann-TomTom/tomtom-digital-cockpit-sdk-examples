---
title: Documentation
submenu: # Additional submenu items for example - blog posts which will be displayed right below table of contents. (not mandatory)
    - title: Links
    - title: "Platform Overview"
      url: "/tomtom-indigo/documentation/platform-overview/what-is-tomtom-indigo"
    - title: "Product Walk-through"
      url: "/tomtom-indigo/documentation/platform-overview/example-apps"
    - title: "Getting Started Guide"
      url: "/tomtom-indigo/documentation/getting-started/introduction"
    - title: "Development Guide"
      url: "/tomtom-indigo/documentation/development/introduction"
    - title: "Integration Guide"
      url: "/tomtom-indigo/documentation/integrating-tomtom-indigo/introduction"
    - title: "Tutorials"
      url: "/tomtom-indigo/documentation/tutorials-and-examples/overview"
---

<iframe
    src="https://player.vimeo.com/video/649985038?h=5a2fa675b4&title=0&portrait=0&color=44ABE0"
    style="aspect-ratio: 16 / 9; width: 100%; margin-bottom: 3rem;"
    frameborder="0"
    allow="autoplay; fullscreen; picture-in-picture;"
    allowfullscreen>
</iframe>

TomTom IndiGO is a framework that delivers Android-based digital cockpits. It includes an 
application platform to support the development of highly integrated applications on top of Android 
Automotive. It also offers a complete set of industrial strength end-user applications that can be 
customized to the needs of a car maker. Take the in-car experience to new places and get started 
with our comprehensive SDK that includes APIs, supporting documentation, and code examples.

Creating a TomTom IndiGO product requires two distinct activities: development of the software using
the TomTom IndiGO SDK, and integrating that software in the vehicle. Development for TomTom IndiGO
is an independent activity, and can be achieved using emulators and mobile devices. We provide
information on both parts here on this developer portal. You can use the site map on the left to
skip ahead, or search for specific content by using the search bar above; just click the magnifying
glass and enter your query.

## API Reference

Developing for TomTom IndiGO requires a set of Application Programming Interfaces (API): the main
TomTom IndiGO API and the TomTom Android Tools API. The reference documentation for both can be
found on the [API reference documentation page](/tomtom-indigo/api-reference/api-reference).
It provides the details of all classes and functions, and insights into the relation between all
modules and packages.

## Releases

Releases and release notes can be found on the [Releases page](/tomtom-indigo/releases/releases). 
The latest Example Application can be downloaded from the TomTom IndiGO external binary repository, 
using the credentials you received after signing the Evaluation Agreement (EA). To request access, 
please go to the [Getting Started](/tomtom-indigo/documentation/getting-started/introduction) 
guide, and "Register for early access".

---

## Useful links

<!-- prettier-ignore -->
<DocsArticles
  articles={
    [
      {
        title: "What is TomTom IndiGO?",
        body: `Read the Platform Overview to find out more about TomTom IndiGO, the concepts and
         the building blocks that make up the platform.`,
        button: {
          label: "Platform Overview",
          href: "/tomtom-indigo/documentation/platform-overview/what-is-tomtom-indigo"
        }
      },
      {
        title: "Example App",
        body: `Check out the functionality of the TomTom IndiGO platform in the walk-through of
        the example application, or get it yourself by following our Getting Started guide.`,
        button: {
          label: "Product walk-through",
          href: "/tomtom-indigo/documentation/platform-overview/example-apps"
        }
      },
      {
        title: "Getting Started",
        body: `Before you can start development with the TomTom IndiGO platform, you'll have to
        make sure your system is set up correctly. The getting started guide will help you set up
        an environment in which TomTom IndiGO development is possible.`,
        button: {
          label: "Getting Started Guide",
          href: "/tomtom-indigo/documentation/getting-started/introduction"
        }
      },
      {
        title: "Developing on TomTom IndiGO",
        body: `When you've set up your development environment, you are ready to start developing
        a TomTom IndiGO application. A good start is to familiarize yourself with the architecture
        and design of TomTom IndiGO. Understanding this will make it easier to grasp the TomTom
        IndiGO concepts of plugins, UI controls, and more.`,
        button: {
          label: "Development Guide",
          href: "/tomtom-indigo/documentation/development/introduction"
        }
      },
      {
        title: "Integrating TomTom IndiGO",
        body: `Read more about the steps of integrating TomTom IndiGO in a vehicle or on other
        hardware.`,
        button: {
          label: "Integration Guide",
          href: "/tomtom-indigo/documentation/integrating-tomtom-indigo/introduction"
        }
      },
      {
        title: "Tutorials",
        body: `When you're familiar with the TomTom IndiGO architecture and the way it names and
        uses plugins and components, you can start following one of the tutorials. A tutorial will
        lead you step-by-step through the process of adding functionality to your product.`,
        button: {
          label: "Tutorials",
          href: "/tomtom-indigo/documentation/tutorials-and-examples/overview"
        }
      },
      {}
    ]
  }
/>

---

## What's new?

<!-- prettier-ignore -->
<DocsArticles
  articles={
    [
      {
        title: "Localization",
        body: `The Android resource framework provides localization support. On top of the standard 
        Android resource framework, TomTom IndiGO provides additional tooling and additional 
        metadata to facilitate localization.`,
        button: {
          label: "Read more",
          href: "/tomtom-indigo/documentation/development/platform-features/localization"
        }
      },
      {
        title: "Tutorial: Add Support for a Custom Non-Android App Type",
        body: `For a custom, non-Android, app type, a means to access the list of available apps to 
        display in the App Launcher and also a means to launch these apps will be required. This 
        tutorial explains how to implement this functionality.`,
        button: {
          label: "Read more",
          href: "/tomtom-indigo/documentation/tutorials-and-examples/app-launcher/add-support-for-a-custom-non-android-app-type"
        }
      },
    ]
  }
/>

---

<Button label="Get Started" href="/tomtom-indigo/documentation/getting-started/introduction" icon="arrow" />
