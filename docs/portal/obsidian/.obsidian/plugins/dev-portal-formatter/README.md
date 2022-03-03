# TomTom Devportal Formatter

This is a custom Obsidian plugin that does the following to ease working with developer documentation:

-   Removes `.md` from links, both within the current file and when files are automatically updated in the background so the link pattern matches the one expected by portal
-   Updates image tags to correctly refer to image path in a way that works for editor and portal.
-   Formats markdown (line lengths, syntax style) using prettier for consistency
-   Updates `navigation.yml` automatically

## Notes

1. The formatting happens automatically, and may cause changes as soon as you open a incorrectly formatted file
2. The `navigation.yml` updater assumes that the title of a page always matches the title in the side bar. It takes a sentence case version of the file name as title if no metadata field is written.
