# Module examples_media_fallbackpolicy

## Example media fallback policy

This is an example demonstrating how to customize the browsing experience for media sources for
which a specific customization is not provided by the TomTom IndiGO stock media player UI.

Every media source installed in the system has its data and information provided to the stock
media player UI via a [`PolicyProvider`](TTIVI_INDIGO_API). When a product hardware platform
already pre-installs all necessary media sources preinstalled, it is advantageous to customize them.
But if the user is allowed to install their own media sources via an app store, then it is hard to
keep track of which media sources are available in the store to customize them.

The stock media player UI contains a predefined fallback [`PolicyProvider`](TTIVI_INDIGO_API) which
is used to display generic contents for non manually customized media sources, but it can be
desirable to replace it with a different one.

The example demonstrates concepts also explained in the
[Customize a media source](https://developer.tomtom.com/tomtom-indigo/documentation/tutorials-and-examples/media/customize-a-media-source)
guide in the TomTom IndiGO documentation.

To be able to try the customizations in this example, other than installing it on a device, it is
also necessary to compile and install the example module [examples_media_source]: that example
contains the media source customized by this example. Trying another example or the template app
will make the differences clearer.

It can be helpful to also install another non customized Android Automotive media source: the
fallback policy's customizations will be then applied to both.
