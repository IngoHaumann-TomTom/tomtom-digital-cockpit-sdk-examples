---
title: Create a Frontend Plugin
---
For an introduction to frontend plugins in the TomTom IndiGO platform, see
([Frontend Plugins](/tomtom-indigo/documentation/development/frontend-plugins))

## Introduction <!-- omit in toc -->
In this example, we will create a new frontend for managing an account on the device. It will
provide a login screen where you can enter a username and a password to login, and if the user is
logged in, you have the option to logout again. We will also add a menu item to the main menu that
will be associated to the new frontend. The final step will be to let the new frontend replace
TomTom IndiGO's user profile frontend.

The source for this example can be found in the following directories in the examples source:
- [`examples/plugin/app/`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/plugin/app)
- [`examples/plugin/frontend/`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree/main/examples/plugin/frontend)

Creating a frontend and the menu item consists of a number of steps:

  - [Creating the frontend class](#creating-the-frontend-class)
    - [Frontend lifecycle methods](#frontend-lifecycle-methods)
    - [Showing panels on the screen](#showing-panels-on-the-screen)
  - [Creating the FrontendBuilder class](#creating-the-frontendbuilder-class)
  - [Creating the panel](#creating-the-panel)
  - [Creating a menu item](#creating-a-menu-item)
  - [Defining the frontend and menu item build config](#defining-the-frontend-and-menu-item-build-config)
  - [Registering the frontend and menu item build config](#registering-the-frontend-and-menu-item-build-config)
- [More information](#more-information)

For more information on all the classes and APIs,
[see the API reference documentation](/tomtom-indigo/api-reference/api-reference).

All the code snippets in this guide can also be found in the TomTom IndiGO example application.

### Creating the frontend class

Create a new frontend by deriving the `Frontend` framework class.

[`src/main/kotlin/com/example/ivi/example/plugin/frontend/AccountFrontend.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/frontend/src/main/kotlin/com/example/ivi/example/plugin/frontend/AccountFrontend.kt#L26)

```kotlin
import com.tomtom.ivi.platform.frontend.api.common.frontend.Frontend
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendContext

internal class AccountFrontend(frontendContext: FrontendContext) : Frontend(frontendContext) {
    // ...
}
```

There are no abstract methods in the [`Frontend`](TTIVI_PLATFORM_API) class, but some methods, like
the lifecycle ones (see below), are good to consider implementing.

#### Frontend lifecycle methods

- `onCreate` - callback when the frontend is created.
- `onDestroy` - callback when the frontend is about to get destroyed.

#### Showing panels on the screen

There are two callbacks for when an event is triggered to show a [`TaskPanel`](TTIVI_PLATFORM_API)
on the screen.

- `createMainTaskPanel` - override it to display a single `TaskPanel` when the UI is shown.
- `openTaskPanels` - override it when more control is needed over which panels should be shown.

__Note:__ A frontend class must override only one of these two methods.

### Creating the frontend-builder class

Add an `AccountFrontendBuilder` class, derived from the [`FrontendBuilder`](TTIVI_PLATFORM_API)
class. Override the `build()` method in the class and return a new instance of the `AccountFrontend`
class.

[`src/main/kotlin/com/example/ivi/example/plugin/frontend/AccountFrontendBuilder.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/frontend/src/main/kotlin/com/example/ivi/example/plugin/frontend/AccountFrontendBuilder.kt#L14-L22)

```kotlin
import com.tomtom.ivi.platform.frontend.api.common.frontend.Frontend
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendBuilder
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendContext

class AccountFrontendBuilder: FrontendBuilder() {

    override fun build(frontendContext: FrontendContext): Frontend =
        AccountFrontend(frontendContext)
}
```

The builder class must follow a specific naming convention. It must have a "FrontendBuilder"
suffix and must start with an upper case character.

### Creating the panel

There are a number of specialised `Panel` classes that can be used in the platform, see package
[`com.tomtom.ivi.platform.frontend.api.common.frontend.panels`](TTIVI_PLATFORM_API)).
For this example we will create a `Panel` class inheriting from the
[`TaskPanel`](TTIVI_PLATFORM_API) class.

A [`TaskPanel`](TTIVI_PLATFORM_API) is typically launched by tapping one of the menu items, like
opening Contacts; or some other UI event, like opening the Climate panel. It encapsulates a task
that the user may perform, typically away from the map, going back to the map when the task is
finished.

Derive from the [`TaskPanel`](TTIVI_PLATFORM_API) class, and override the
`createInitialFragmentInitializer()` method, which should return a new instance of the
[`IviFragment`](TTIVI_PLATFORM_API) class (described further down).

[`src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginPanel.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/frontend/src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginPanel.kt#L18-L23)

```kotlin
internal class AccountLoginPanel(frontendContext: FrontendContext) :
    TaskPanel(frontendContext) {

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(AccountLoginFragment(), this)
}
```

Also create a `ViewModel` class, derived from the [`FrontendViewModel`](TTIVI_PLATFORM_API) class.
The `ViewModel` is the ViewModel in the Model-View-ViewModel (MVVM) pattern, whose role is to
expose streams of data relevant to the view and streams of events to the model.

[`src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginViewModel.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/frontend/src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginViewModel.kt#L34-L41)

```kotlin
internal class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    val username = MutableLiveData("")
    val password = MutableLiveData("")
    // ...

    fun onLoginClick() {
        // ...
    }
}
```

Finally create a `Fragment` class, derived from [`IviFragment`](TTIVI_PLATFORM_API) and using the
newly created `Panel` and `ViewModel` classes, overriding the `viewFactory` property. The
TomTom IndiGO platform is designed to work well with the MVVM pattern, and this is used in the
`onCreateView` callback as a convenience to inflate a data binding layout and using that in the
fragment. If an `onCreateView` custom implementation still is preferred, the `viewFactory` property
can be left as null instead.

[`src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginFragment.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/frontend/src/main/kotlin/com/example/ivi/example/plugin/frontend/login/AccountLoginFragment.kt#L17-L21)

```kotlin
internal class AccountLoginFragment :
    IviFragment<AccountLoginPanel, AccountLoginViewModel>(AccountLoginViewModel::class) {

    override val viewFactory = ViewFactory(TtiviAccountLoginFragmentBinding::inflate)
}
```

See [this page](https://developer.android.com/topic/libraries/data-binding/expressions), for more
information on how data-binding works in Android and how the `ViewModel` class binds to the XML
layout. It also explains how the `TtiviAccountLoginFragmentBinding` class that you pass in to the
ViewFactory, is auto generated.

### Creating a menu item

In this tutorial a menu item is added to the main menu that will open the main task panel of the
`AccountFrontend`. To add the menu item to the main menu we need a [`MenuItem`](TTIVI_PLATFORM_API)
instance.

Create an `AccountMenuItem.kt` file, add a property in the file and assign it a
[`MenuItem`](TTIVI_PLATFORM_API) instance. The name of the property must follow a specific naming
convention. It must have a "MenuItem" suffix and must start with a lower case character.

[`src/main/kotlin/com/example/ivi/example/plugin/frontend/AccountMenuItem.kt`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/frontend/src/main/kotlin/com/example/ivi/example/plugin/frontend/AccountMenuItem.kt#L16-L21)

```kotlin
val accountMenuItem = MenuItem(
    AccountFrontend::class.qualifiedName!!,
    R.drawable.ttivi_account_menuitem,
    R.string.ttivi_account_menuitem_name
)
```

The [`MenuItem`](TTIVI_PLATFORM_API) constructor takes a unique ID, a `DrawableResolver`, and a
`StringResolver`. The latter two resolve the icon and the name of the menu item. In the above
example the resolvers are defined as Android resources.

### Defining the frontend and menu item build config

Create the frontend and menu item build configurations. These configurations will be used to
register the frontend and the menu item to the framework at build time.

Define a frontend implementation and a menu item implementation. These can also be defined in a
top-level Gradle file (for example `frontends-and-menuitems.gradle.kts`) so it can be used in a
multi-project build, including the tests.

Create an
[`examples/plugin/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/app/build.gradle.kts#L26)
file:

```kotlin
import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendCreationPolicy
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig

/**
 * Defines the implementation and the configuration of the account frontend.
 */
val accountFrontend = FrontendConfig(
    // Needs to match with the name of the builder class.
    frontendBuilderName = "AccountFrontendBuilder",
    // The module containing the frontend implementation.
    implementationModule = ExampleModuleReference("examples_plugin_frontend"),
    // Create the frontend on demand. It will be created when the menu item is selected.
    creationPolicy = FrontendCreationPolicy.CREATE_ON_DEMAND
)

// We can use `FrontendConfig.toMenuItem()` as the menu item is defined in the same module as
// the frontend implementation. The argument given needs to match with the property that
// was created earlier in the tutorial.
val accountMenuItem = accountFrontend.toMenuItem("accountMenuItem")
```

The above build configurations use the `ExampleModuleReference` to resolve a module name into
the full-qualified package. It is defined once and used for all configurations. See
[Integrate TomTom IndiGO into a Gradle Project](/tomtom-indigo/documentation/tutorials-and-examples/setup/integrate-tomtom-indigo-into-a-gradle-project#module-references)
for details.

### Registering the frontend and menu item build config

The last step is to register the frontend and the menu item to build configurations in the main
application's build script.

Modify the
[`examples/plugin/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/app/build.gradle.kts#L25-L50)
file:

```kotlin
import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendCreationPolicy
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    // Apply the plugin to use default frontends and services from TomTom IndiGO Platform
    // and from all TomTom IndiGO Applications (from appsuite).
    id("com.tomtom.ivi.product.defaults.core")
}

// Create `accountFrontend` and `accountMenuItem`
val accountFrontend = FrontendConfig(
    // Needs to match with the name of the builder class.
    frontendBuilderName = "AccountFrontendBuilder",
    // The module containing the frontend implementation.
    implementationModule = ExampleModuleReference("examples_plugin_frontend"),
    // Create the frontend on demand. It will be created when the menu item is selected.
    creationPolicy = FrontendCreationPolicy.CREATE_ON_DEMAND
)

// We can use `FrontendConfig.toMenuItem()` as the menu item is defined in the same module as
// the frontend implementation. The argument given needs to match with the property that
// was created earlier in the tutorial.
val accountMenuItem = accountFrontend.toMenuItem("accountMenuItem")

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                // Use the default frontends and menu items as defined by the plugin applied above:
                // `com.tomtom.ivi.product.defaults.core`.
                applyGroups { includeDefaultGroups() }
                frontends {
                    // Register the `accountFrontend`.
                    add(accountFrontend)
                }
                menuItems {
                    // Register the `accountMenuItem` and associate it with the `accountFrontend`.
                    addLast(accountMenuItem to accountFrontend)
                }
            }
        }
    }
}

// The rest of the build script, dependencies, etc.
```

The above example adds the `accountFrontend` and the `accountMenuItem` to the default IVI
instance. A vehicle may have multiple infotainment screens. Each infotainment screen is an IVI
instance. See
[Configure the Runtime Deployment of the IVI System](/tomtom-indigo/documentation/tutorials-and-examples/deployment/configure-the-runtime-deployment-of-the-ivi-system)
for more details about IVI instance configurations.

The final step is to let the new frontend replace TomTom IndiGO's user profile frontend. For this
we have to use `replace` instead of `add`. The same applies for the user profile menu item.

Modify the
[`examples/plugin/app/build.gradle.kts`](https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/blob/main/examples/plugin/app/build.gradle.kts#L43-L48)
file:

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.plugin.defaultsplatform.userProfileFrontend
import com.tomtom.ivi.platform.gradle.api.plugin.defaultsplatform.userProfileMenuItem
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

plugins {
    // Apply the Gradle plugin to define the default frontends, menu items and services from
    // TomTom IndiGO Platform and from all TomTom IndiGO Applications (from the appsuite). The
    // default frontends, menu items and services are defined in groups. The groups are applied
    // to the IVI application configuration below.
    id("com.tomtom.ivi.product.defaults.core")
}

// Create `accountFrontend` and `accountMenuItem`.
val accountFrontend = FrontendConfig(
    frontendBuilderName = "AccountFrontendBuilder",
    implementationModule = ExampleModuleReference("examples_plugin_frontend"),
    creationPolicy = FrontendCreationPolicy.CREATE_ON_DEMAND
)
val accountMenuItem = accountFrontend.toMenuItem("accountMenuItem")

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                // Configure all frontends and menu items from all groups that do not require an
                // explicit opt-in. The groups are defined by the
                // `com.tomtom.ivi.platform.defaults.core` Gradle plugin.
                applyGroups { includeDefaultGroups() }

                // Replace TomTom IndiGO's user profile frontend with the `accountFrontend`.
                frontends {
                    replace(userProfileFrontend, accountFrontend)
                }

                // Replace TomTom IndiGO's user profile menu item with the `accountMenuItem`
                // and associate it with the `accountFrontend`.
                menuItems {
                    replace(userProfileMenuItem, accountMenuItem to accountFrontend)
                }
            }
        }
    }
}

// The rest of the build script, dependencies, etc.
```

The above example replaces the `userProfileFrontend` with the `accountFrontend` and replaces
the `userProfileMenuItem` with the `accountMenuItem`.


## More information

For information on how to call the [`@IviServiceApi`](TTIVI_PLATFORM_API) members from
[Frontend Plugins](/tomtom-indigo/documentation/development/frontend-plugins)
refer to section
[Calling service methods](/tomtom-indigo/documentation/development/ivi-services#calling-service-methods).
