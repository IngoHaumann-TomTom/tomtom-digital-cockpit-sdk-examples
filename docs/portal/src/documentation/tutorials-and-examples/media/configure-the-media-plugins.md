---
title: Configure the Media Plugins
---

The off-the-shelf media plugin comes with default configuration. This configuration provides a
default behavior, such as showing a mini player when music is playing. This configuration is based
on the
[configuration-framework](/tomtom-indigo/documentation/tutorials-and-examples/customization/use-the-configuration-framework)
and can be changed if necessary. This guide explains this procedure.

If you want to configure the media frontend plugin for specific media sources, you can follow
this guide:
[How to customize a media source](/tomtom-indigo/documentation/tutorials-and-examples/media/customize-a-media-source).

## Media plugin default configuration

The media plugin default configuration is defined as a resource file that contains configuration
keys and their values. The following keys and values are defined in the media plugin:

```xml
<resources>
    <!-- Indicates whether the media frontend has a mini player or not; `true` by default. -->
    <bool name="hasMiniPlayerConfigKey">true</bool>
</resources>
```

## Change the media plugin configuration

The media plugin default configuration can be changed by adding a custom configuration resource file
in your application, such as `<module>/res/value/ttivi-media-configuration.xml`.

![media configuration](images/media_configuration_file.png)

The custom configuration overrides the default values with the one provided.

## Configuring the mini player

When audio is playing, a mini player is shown on the home screen.

![mini player](images/media_mini_player.png)

If you don't want to use the mini player provided with the off-the-shelf media plugin, or you
want to implement your own frontend plugin for the mini player, you can configure it by setting the
`hasMiniPlayerConfigKey` to `false`:

```xml
<bool name="hasMiniPlayerConfigKey">false</bool>
```
