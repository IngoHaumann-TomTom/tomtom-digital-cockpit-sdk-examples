---
title: Configure Debug Tabs
---

The TomTom IndiGO distribution comes integrated with a non-user-facing feature useful for
development: a debug menu. By default, it can be opened by long-pressing the `volume down` button,
via the backtick ("\`") key, or via ADB with `adb shell input keyevent --longpress KEYCODE_GRAVE`.

## Adding a debug tab

To add a new debug tab, an example is provided in the example app with
`com.tomtom.ivi.example.debugtab.activityview.activityViewDebugTabFrontendExtension`.

The debug tab's fragment is made with
[`DebugTabFragment`](TTIVI_INDIGO_API) and the view model with a
normal [`FrontendViewModel`](TTIVI_INDIGO_API),
templated with the [`TabbedDebugPanel`](TTIVI_INDIGO_API) type.

The debug tab is then registered in the debug menu with a
[`DebugTabFrontendExtension`](TTIVI_INDIGO_API).

The product's `build.gradle.kts` file should then be changed to include, in the `ivi` configuration,
a customization of the debug frontend:

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.plugin.defaultsplatform.userProfileFrontend
import com.tomtom.ivi.platform.gradle.api.plugin.defaultsplatform.userProfileMenuItem
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val debugFrontend by extra {
    FrontendConfig(
        frontendBuilderName = "DebugFrontendBuilder",
        implementationModule = IviPlatformModuleReference("platform_debug_plugin_frontend_debug")
    )
}

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                useDefaults()
                frontends {
                    configureIfPresent(debugFrontend) {
                        addExtension(activityViewDebugTabFrontendExtension)
                    }
                }
            }
        }
    }
}
```

For more information about panels, fragments, view models, and adding frontend extensions, see the
[frontend plugin](/tomtom-indigo/documentation/tutorials-and-examples/basics/create-a-frontend-plugin)
how-to guide.

## Replacing debug tabs

Similarly to adding debug tabs, the same sort of `ivi` configuration must be used.
However, the `debugFrontend` configuration must be changed by adding the `extensions` parameter,
which takes a list of debug tab
[`DebugTabFrontendExtension`](TTIVI_INDIGO_API)s.

