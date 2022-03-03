## Introduction

This directory contains the source files for the Developer Portal.

The documentation in the `/src` folder covers the following 3 sections on the Portal:

-   [Documentation](https://developer.tomtom.com/indigo/documentation)
-   [API Reference](https://developer.tomtom.com/indigo/api-reference)
-   [Releases](https://developer.tomtom.com/indigo/releases)

## Getting started

If you want to add new documentation or need to edit the existing source files, please follow these
guidelines:
[Writing Documentation for the Developer Portal](https://confluence.tomtomgroup.com/display/SSAUTO/Writing+Documentation+for+the+Developer+Portal).

Follow these instructions if you wish to upload your changes to the Developer Portal staging
environment:
[How to upload to the Developer Portal](https://confluence.tomtomgroup.com/display/SSAUTO/How+to+upload+to+the+Developer+Portal)

## Tools

There is some tooling to automatically generate API links and check for broken links.
See the
[Writing Documentation](https://confluence.tomtomgroup.com/display/SSAUTO/Writing+Documentation+for+the+Developer+Portal)
guide for more info on adding API links.
You can verify whether the API links and URLs are (still) correct with the following task:

```bash
./gradlew portal_check
```

## Obsidian

The `./obsidian` folder contains configuration for the [Obsidian Markdown editor](https://obsidian.md/). This can optionally be used to edit documentation and is optimized for certain uses such as adding images, formatting tables and working with custom components.
