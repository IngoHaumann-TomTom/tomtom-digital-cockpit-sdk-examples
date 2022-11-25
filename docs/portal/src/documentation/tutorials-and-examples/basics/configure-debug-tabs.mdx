---
title: Configure Debug Tabs
---

The TomTom Digital Cockpit distribution comes integrated with a non-user-facing feature useful for
development: a debug menu. By default, it can be opened by long-pressing the `volume down` button,
via the backtick ("\`") key, or via ADB with `adb shell input keyevent --longpress KEYCODE_GRAVE`.

## Adding a debug tab

To add a new debug tab, an example app is provided in the
[`examples/debugtab`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/debugtab)
directory, with
`com.tomtom.ivi.platform.debug.api.frontendextension.debugtab.DebugTabFrontendExtension`.

The debug tab's fragment is made with the [`DebugTabFragment`](TTIVI_PLATFORM_API) and the view
model with a normal [`FrontendViewModel`](TTIVI_PLATFORM_API), templated with the
[`TabbedDebugPanel`](TTIVI_PLATFORM_API) type.

The debug tab is then registered in the debug menu with a
[`DebugTabFrontendExtension`](TTIVI_PLATFORM_API).

The product's
[`build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/debugtab/build.gradle.kts#L12-L51)
file should then be changed to include, in the `ivi` configuration, a customization of the debug
frontend:

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.dependencies.ModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendExtensionConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.defaults.config.debugFrontend
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val debugTabFrontendExtension = FrontendExtensionConfig(
    frontendExtensionName = "debugTabFrontendExtension",
    implementationModule = ModuleReference(
        "com.example.ivi",
        "examples_debugtab",
        "com.example.ivi.example.debugtab"
    )
)

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                applyGroups { includeDefaultGroups() }
                frontends {
                    configure(debugFrontend) {
                        addExtension(debugTabFrontendExtension)
                    }
                }
            }
        }
    }
}
```

For more information about panels, fragments, view models, and adding frontend extensions, see the
[frontend plugin](/tomtom-digital-cockpit/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
how-to guide.

## Replacing debug tabs

Similarly to adding debug tabs, the same sort of `ivi` configuration must be used.
However, the `debugFrontend` configuration must be changed by adding the `extensions` parameter,
which takes a list of [`DebugTabFrontendExtension`](TTIVI_PLATFORM_API) objects.
