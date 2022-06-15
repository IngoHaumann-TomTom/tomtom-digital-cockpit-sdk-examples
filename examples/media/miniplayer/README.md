# Module examples_media_miniplayer

## Example custom media mini player

This is an example demonstrating how to control media playback with a customized UI visible when
not browsing for media in the stock media player UI.
The stock media player UI uses a process panel to display what is currently being played, but it
can be disabled when necessary: for example when media controls are always present in the UI.

The example demonstrates concepts explained in the following guides from the TomTom IndiGO
documentation:

- [Configure the Media Plugins](https://developer.tomtom.com/tomtom-indigo/documentation/tutorials-and-examples/media/configure-the-media-plugins)
- [Customize a media source](https://developer.tomtom.com/tomtom-indigo/documentation/tutorials-and-examples/media/customize-a-media-source)

To be able to try the customizations in this example, other than installing it on a device, it is
also necessary to compile and install the example module [examples_media_source]: that example
contains the media source customized by this example. Trying another example or the template app
will make the differences clearer.

## System UI integration note

This example only demonstrates how to make a new media playback UI; it does not provide an example
on how to place this UI in a different part of the screen, or on another display.

To accomplish those kinds of modifications, the
[System UI guide](https://developer.tomtom.com/tomtom-indigo/documentation/development/system-ui)
contains more information about customizing the UI and create new panel types to integrate in it.
