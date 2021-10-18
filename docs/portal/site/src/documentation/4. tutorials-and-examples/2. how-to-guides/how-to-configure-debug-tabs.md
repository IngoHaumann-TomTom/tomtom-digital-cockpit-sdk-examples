---
title: How to Configure Debug Tabs
layout: default
---

The stock distribution of IndiGO contains a non-user-facing feature useful for development: a debug
menu. By default, it can be opened by long-pressing the `volume down` button, via the backtick
("\`") key, or via ADB with `adb shell input keyevent --longpress KEYCODE_GRAVE`.

## Adding a debug tab

To add a new debug tab, an easy example is provided in the example app with
[ActivityViewDebugTab](com.tomtom.ivi.example.debugtab.activityview.activityViewDebugTabFrontendExtension).

The debug tab's fragment is made with
[DebugFragmentTab](com.tomtom.ivi.api.common.debugtab.DebugFragmentTab) and the view model with a
normal [FrontendViewModel](com.tomtom.ivi.api.framework.frontend.viewmodels.FrontendViewModel),
templated with the [TabbedDebugPanel](com.tomtom.ivi.api.common.debugtab.TabbedDebugPanel) type.

The debug tab is then registered in the debug menu with a
[DebugTabFrontendExtension](com.tomtom.ivi.api.common.debugtab.DebugTabFrontendExtension).

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
        implementationModule = IviPlatformModuleReference("stock_frontends_debug")
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
[frontend plugin](/indigo/documentation/tutorials-and-examples/how-to-guides/how-to-create-a-frontend-plugin)
how-to guide.

## Replacing debug tabs

Similarly to adding debug tabs, the same sort of `ivi` configuration must be used.
However, the `debugFrontend` configuration must be changed by adding the `extensions` parameter,
which takes a list of debug tab
[DebugTabFrontendExtension](com.tomtom.ivi.api.common.debugtab.DebugTabFrontendExtension)s.
