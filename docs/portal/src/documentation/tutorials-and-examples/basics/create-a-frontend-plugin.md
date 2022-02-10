---
title: Create a Frontend Plugin
---
For an introduction to frontend plugins in the TomTom IndiGO platform, see
([Frontend Plugins](/tomtom-indigo/documentation/development/frontend-plugins))

## Introduction
In this example, we will create a new frontend for managing an account on the device. It will
provide a login screen where you can enter a username and a password to login, and if the user is
logged in, you have the option to logout again. We will also add a menu item to the main menu that
will be associated to the new frontend. The final step will be to let the new frontend replace
TomTom IndiGO's user profile frontend.

Creating a frontend and the menu item consists of a number of steps:

- [Creating the `Frontend` class, deriving the abstract `Frontend` class.](#creating-the-frontend-class)
- [Creating the `FrontendBuilder` class.](#creating-the-frontendbuilder-class)
- [Creating the `Panel` class, and a `Fragment` to display the content on the screen.](#creating-the-panel)
- [Creating a `MenuItem`.](#creating-a-menu-item)
- [Defining the frontend and menu item build config.](#defining-the-frontend-and-menu-item-build-config)
- [Registering the frontend and menu item build config.](#registering-the-frontend-and-menu-item-build-config)

For more information on all the classes and APIs,
[see the API reference documentation](/tomtom-indigo/api-reference/tomtom-indigo-api-reference).

All the code snippets in this guide can also be found in the TomTom IndiGO example application.

### Creating the frontend class

Create a new frontend by deriving the `Frontend` framework class.

```kotlin
import com.tomtom.ivi.platform.frontend.api.common.frontend.Frontend
import com.tomtom.ivi.platform.frontend.api.common.frontend.FrontendContext

internal class AccountFrontend(frontendContext: FrontendContext) : Frontend(frontendContext) {
    // ...
}
```

There are no abstract methods in the [`Frontend`](TTIVI_INDIGO_API) class, but some methods, like
the lifecycle ones (see below), are good to consider implementing.

#### Frontend lifecycle methods

- `onCreate` - callback when the frontend is created.
- `onDestroy` - callback when the frontend is about to get destroyed.

#### Showing panels on the screen

There are two callbacks for when an event is triggered to show a [`TaskPanel`](TTIVI_INDIGO_API) on
the screen.

- `createMainTaskPanel` - override it to display a single `TaskPanel` when the UI is shown.
- `openTaskPanels` - override it when more control is needed over which panels should be shown.

**Note:** A frontend class must override only one of these two methods.

### Creating the FrontendBuilder class

Add an `AccountFrontendBuilder` class, derived from [`FrontendBuilder`](TTIVI_INDIGO_API) class.
Override the `build()` method in the class and return a new instance of the `AccountFrontend` class.

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
[`com.tomtom.ivi.platform.frontend.api.common.frontend.panels`](TTIVI_INDIGO_API)).
For this example we will create a `Panel` class inheriting from the [`TaskPanel`](TTIVI_INDIGO_API)
class.

A [`TaskPanel`](TTIVI_INDIGO_API) is typically launched by tapping one of the menu items, like
opening Contacts; or some other UI event, like opening the Climate panel. It encapsulates a task
that the user may perform, typically away from the map, going back to the map when the task is
finished.

Derive from the [`TaskPanel`](TTIVI_INDIGO_API) class, and override the
`createInitialFragmentInitializer()` method, which should return a new instance of the
[`IviFragment`](TTIVI_INDIGO_API) class (described further down).

```kotlin
internal class AccountLoginPanel(frontendContext: FrontendContext) :
    TaskPanel(frontendContext) {

    override fun createInitialFragmentInitializer() =
        IviFragment.Initializer(AccountLoginFragment(), this)
}
```

Also create a `ViewModel` class, derived from the [`FrontendViewModel`](TTIVI_INDIGO_API) class.
The `ViewModel` is the ViewModel in the Model-View-ViewModel (MVVM) pattern, whose role is to
expose streams of data relevant to the view and streams of events to the model.

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

Finally create a `Fragment` class, derived from [`IviFragment`](TTIVI_INDIGO_API) and using the
newly created `Panel` and `ViewModel` classes, overriding the `viewFactory` property. The
TomTom IndiGO platform is designed to work well with the MVVM pattern, and this is used in the
`onCreateView` callback as a convenience to inflate a data binding layout and using that in the
fragment. If an `onCreateView` custom implementation still is preferred, the `viewFactory` property
can be left as null instead.

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
`AccountFrontend`. To add the menu item to the main menu we need a [`MenuItem`](TTIVI_INDIGO_API)
instance.

Create an `AccountMenuItem.kt` file, add a property in the file and assign it a
[`MenuItem`](TTIVI_INDIGO_API) instance. The name of the property must follow a specific naming
convention. It must have a "MenuItem" suffix and must start with a lower case character.

```kotlin
val accountMenuItem = MenuItem(
    AccountFrontend::class.qualifiedName!!,
    R.drawable.ttivi_account_menuitem,
    R.string.ttivi_account_menuitem_name
)
```

The [`MenuItem`](TTIVI_INDIGO_API) constructor takes a unique ID, a `DrawableResolver` and a
`StringResolver`. The latter two resolve the icon and the name of the menu item. In the above
example the resolvers are defined as Android resources.

### Defining the frontend and menu item build config

Create the frontend and menu item build configurations. These configurations will be used to
register the frontend and the menu item to the framework at build time.

Define a frontend implementation and a menu item implementation in the top-level
`frontends-and-menuitems.gradle.kts` file so it can be used in all projects, including tests.

Create `<rootDir>/frontends-and-menuitems.gradle.kts`:

```kotlin
import com.tomtom.ivi.buildsrc.dependencies.ExampleModuleReference
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendCreationPolicy
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig

/**
 * Defines the implementation and the configuration of the account frontend.
 */
val accountFrontend by extra {
    FrontendConfig(
        // Needs to match with the name of the builder class.
        frontendBuilderName = "AccountFrontendBuilder",
        // The module containing the frontend implementation.
        implementationModule = ExampleModuleReference("frontends_account"),
        // Create the frontend on demand. It will be created when the menu item is selected.
        creationPolicy = FrontendCreationPolicy.CREATE_ON_DEMAND
    )
}

val accountMenuItem by extra {
    // We can use `FrontendConfig.toMenuItem()` as the menu item is defined in the same module as
    // the frontend implementation. The argument given needs to match with the property that
    // was created earlier in the tutorial.
    accountFrontend.toMenuItem("accountMenuItem")
}
```

The above build configurations use the `ExampleModuleReference` to resolve a module name into
the full-qualified package. It is defined once and used for all configurations. See
[Integrate TomTom IndiGO into a Gradle Project](/tomtom-indigo/documentation/tutorials-and-examples/setup/integrate-tomtom-indigo-into-a-gradle-project#module-references)
for details.

### Registering the frontend and menu item build config

The last step is to register the frontend and the menu item to build configurations in the main
application's build script.

Create `modules/products/exampleapp/build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

// Define the frontends and menu items as defined in the top-level
// `frontends-and-menuitems.gradle.kts` file.
apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))

// Use Gradle's extra extensions to obtain the `accountFrontend` and `accountMenuItem` configs as
// defined in the top-level `frontends-and-menuitems.gradle.kts` file.
val accountFrontend: FrontendConfig by project.extra
val accountMenuItem: MenuItemConfig by project.extra

plugins {
    // Apply the plugin to use default frontends and services from TomTom IndiGO Platform
    // and from all TomTom IndiGO Applications (from appsuite).
    id("com.tomtom.ivi.product.defaults.core")
}

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                // Use the default frontends and menu items as defined by the plugin applied above:
                // `com.tomtom.ivi.product.defaults.core`.
                useDefaults()
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
we have to use 'replace' instead of `add`. The same applies for the user profile menu item.

Create `modules/products/exampleapp/build.gradle.kts`:

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.FrontendConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.MenuItemConfig
import com.tomtom.ivi.platform.gradle.api.plugin.defaultsplatform.userProfileFrontend
import com.tomtom.ivi.platform.gradle.api.plugin.defaultsplatform.userProfileMenuItem
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

// Define the frontends and menu items as defined in top-level `frontends-and-menuitems.gradle.kts`
// file.
apply(from = rootProject.file("frontends-and-menuitems.gradle.kts"))

// Use Gradle's extra extensions to obtain the `accountFrontend` and `accountMenuItem` configs as
// defined in the top-level `frontends-and-menuitems.gradle.kts` file.
val accountFrontend: FrontendConfig by project.extra
val accountMenuItem: MenuItemConfig by project.extra

plugins {
    // Apply the plugin to use the default frontends and services from the TomTom IndiGO platform
    // and app suite.
    id("com.tomtom.ivi.product.defaults.core")
}

ivi {
    application {
        enabled = true
        iviInstances {
            create(IviInstanceIdentifier.default) {
                // Use the default frontends and menu items as defined by the plugin applied above:
                // `com.tomtom.ivi.product.defaults.core`.
                useDefaults()
                frontends {
                    // Replace the TomTom IndiGO's user profile frontend with the `accountFrontend`.
                    replace(userProfileFrontend, accountFrontend)
                }
                menuItems {
                    // Replace the TomTom IndiGO's user profile menu item with the `accountMenuItem`
                    // and associate it with the `accountFrontend`.
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
