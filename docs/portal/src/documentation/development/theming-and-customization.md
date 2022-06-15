---
title: Theming and Customization
---

## Theming

The look of TomTom IndiGO is determined by the theme. A theme consists of a number of styles, each 
of which focus on a different category of themeable attributes, like colors or spacing. You can 
change the styles used in the theme to adapt the look to match your brand. This page explains the 
details of theming and how to customize it.

TomTom IndiGO's theming mechanism is heavily based on
[Android's theming approach](https://developer.android.com/guide/topics/ui/look-and-feel/themes), 
while additionally providing a means to switch themes at runtime through a service. The Android
styles within the theme are applied to the context of the system UI. All the fragments hosted in the
system UI will get that context with the correct styles applied. Similarly, when a theme changes at
run-time, like when switching between light and dark mode themes, all views are created with a new
context that has the new styles applied.

The theme is provided by the [`ThemingService`](TTIVI_INDIGO_API). This service is responsible for
deciding which theme should be used by the system UI. It loads the styles that themes can use from
all discoverable [`ThemeComponentProviderService`](TTIVI_INDIGO_API) instances. (You can read more 
on discoverable services in the documentation on 
[`IviDiscoverableServiceIdProvider`](TTIVI_INDIGO_API).) You can add your own instances, or replace 
the default one with custom instances that provide alternative styles.

__Note:__ The [`ThemingService`](TTIVI_INDIGO_API) works with [`IviTheme`](TTIVI_INDIGO_API)s, which 
are explained in detail in the [Customization](#customization) section.

The styles must provide values for _all_ attributes within their respective categories. Failing to
do so will result in unexpected behavior as the views are missing some of their attributes. The
available attributes are defined within the
[`platform_theming_api_common_attributes`](TTIVI_INDIGO_API) and `core_theme` modules.

The `core_theme` is a _TomTomAndroidTools_ module which defines common theme attributes that can be
used in various projects. The [`platform_theming_api_common_attributes`](TTIVI_INDIGO_API) module,
which extends `core_theme`, also defines extra theme attributes for TomTom IndiGO. You can customize 
the visual appearance of your product by providing the desired values of these attributes.

![Theming high-level diagram](images/theming-high-level-diagram.svg)

If you want a look based on the TomTom UX design specification, you can use the
`platform_theming_api_stock_theme`. This theme supports a light and a dark mode. The dark mode is
the default one for TomTom IndiGO.

If you want a customized theme, you must add your own
[`ThemeComponentProviderService`](TTIVI_INDIGO_API) to provide the extra styles for the
[`ThemingService`](TTIVI_INDIGO_API) to discover. In that implementation you can override the values
of the `platform_theming_api_stock_theme` to suit your look, or replace the
`platform_theming_api_stock_theme` entirely.

### Naming Convention

The naming convention of theming attributes follows the `prefix_what_where_which_quality_quantity`
format. Taking color attributes as an example, the attribute names consist of:

- `prefix`  for all attributes defined in `core_theme` this is `tt`. The prefix for TomTom IndiGO 
  specific component attributes, which are defined in
  [`platform_theming_api_common_attributes`](TTIVI_INDIGO_API) is `ttivi`.
- `what` determines the _background color_.
- `where`_)*_ determines the _place_ where it is used.
- `which` for all color attributes is `color`.
- `quality`_)*_ represents the _type of emphasis_.
- `quantity`_)*_ determines the _level of emphasis_.

_)*_ `where`, `quality` and `quantity` are optional.

In the [Color System](#color-system) section we'll explain how TomTom IndiGO classifies 
_background colors_ and _emphasis_, among other things, to achieve a coherent UI design.

Some examples for color attributes are:

| Prefix | What      | Where    | Which  | Quality    | Quantity |
| ------ | --------- | -------- | ------ | ---------- | -------- |
| tt     | _primary  |          | _color |            |          |
| tt     | _primary  | _content | _color | _emphasis  | _high    |
| tt     | _primary  | _content | _color | _emphasis  | _medium  |
| tt     | _primary  | _content | _color | _emphasis  | _low     |
| tt     | _primary  | _content | _color | _subdued   |          |
| tt     | _primary  | _content | _color | _highlight |          |
| ttivi  | _mainmenu | _content | _color | _emphasis  | _high    |

__Note:__ All attributes defined within the same `prefix_what_where_...` are called a _group_.

## Theming design concepts

### Theming categories

The appearance of the UI can be affected by colors, margins, font types, etc. To reduce the
duplication when creating a new theme and to be able to apply styles independently, these attributes
are defined in categories like color, spacing, font, etc. Each category represents a set of
attributes for the theme.

By having categories of theming attributes, you can create a slightly different theme based on any
other theme. For example, you can create a new look by keeping all categories unchanged, except the
color category. This way you do not need to duplicate _all_ the attributes, but only attributes that
you _want_ to change.

The theme attributes are defined using the standard Android `declare-styleable`. Some examples of
how categories are defined, and which categories attributes belong to, are:

```xml
<!-- attrs_dimens_spacing.xml -->
<resources>
    <declare-styleable name="TtiviThemeCategoryDimensSpacing">
        <attr name="tt_spacing_s" format="dimension" />
        <attr name="tt_spacing_m" format="dimension" />
        <attr name="tt_spacing_l" format="dimension" />
    </declare-styleable>
</resources>
 
<!-- attrs.xml -->
<resources>
    <declare-styleable name="TtiviThemeCategoryStyles">
        <attr name="ttivi_navigation_search_input_text_appearance_style" format="reference" />
    </declare-styleable>
</resources>
 
<!-- attrs_colors.xml -->
<resources>
    <declare-styleable name="TtiviThemeCategoryColors">
        <attr name="tt_surface_content_color_emphasis_high" format="color" />
    </declare-styleable>
</resources>
```

### Color system

The color system helps you to apply colors to your UI, including your brand colors. Colors provide a
hierarchy of information, give the correct meaning to UI elements, and meet legibility and contrast
standards.

The TomTom IndiGO color system is designed with a focus on: hierarchy, background colors, 
content colors, and emphasis.

#### Hierarchy

UI colors are based on the role they play in the interface. For example, the color of the _surface_
will determine if the UI as a whole is light or dark.

By defining which colors can go on top of each other, you can ensure enough contrast to distinguish
UI elements.

#### Background colors

![Background colors](images/theming-background-colors.png)

The background colors are classified into:

- _Surface colors_ that affect the background of the components. The surface is the background of
  all panels, notifications, the process panel, etc.
- _Primary colors_ that are used for primary actions like primary buttons.
- _Secondary colors_ that are used for secondary actions like secondary buttons, the background of
  segmented controls, etc.
- _Destructive and acceptance colors_ that are used for events that require immediate action or a
  decision of the user.
- _Overlay color_ that is used for components that come on top of the surfaces such as overlays,
  pop-overs, etc.
- _Main menu colors_ that are used for the active app icon, inactive icons, labels, and the 
  background of the main menu.
- _Control center colors_ that are used for elements in the control bar, and the background and
  system icons on top of it.

#### Content colors

![Content colors](images/theming-content-colors.png)

Content colors refer to the color of elements that are displayed on top of the background colors.
Whenever UI elements such as texts or icons appear on top of these colors, they will have a
different emphasis to create an order between them.

If there is selected content on the surface like a toggle button, then that selected state is part
of a content color group.

#### Emphasis

![Emphasis](images/theming-emphasis.png) ![Emphasis alert](images/theming-emphasis-alert.png)

A UI consists of many components. The fewer components, the easier it will be to understand what is
being communicated. However, sometimes it cannot be avoided to have more components and then
emphasis is used to make some of them stand out and become the focus of the user.

For important information and actions, a high emphasis color should be used. This prevents
distractions from less important components in the UI, like a separation line. Those less important
components could use a low emphasis color instead.

Emphasis is classified into:

- _High emphasis_ which commands the most attention. High emphasis components may be accompanied by
  _medium_ and _low_ emphasis components that perform a less important role.
- _Subdued state_ which is advised for a component that is not interactive, and as such should be
  de-emphasized. For example: dividers.
- _Highlight state_ which communicates when a user has highlighted a component. It is usually
  combined with another, usually primary color. For example: a focused search view.
- _Accent color_ which is used to indicate that some action is needed from the user, usually in
  combination with a highlight color. For example: an active state in a search view.
- _Critical and success states_ which indicate errors and successful actions. For example: an
  invalid text in a text field.
- _Alert, warning and caution_ which are used to raise the user’s attention. For example: a traffic
  warning on the planned route.

## Customization

Here we'll dive deeper into the way you can customize your product using the tools that theming
provides.

An [`IviThemeComponent`](TTIVI_INDIGO_API) contains the necessary style information and it belongs 
to a single [`IviThemeCategory`](TTIVI_INDIGO_API). The theme component styles are defined as 
Android [style](https://developer.android.com/guide/topics/resources/style-resource) resources. For 
example, a Noto Sans font style is defined as:

```xml
<resources>
    <style name="TtiviThemeFontNotoSans">
        <item name="tt_font_thin">@font/noto_sans_regular</item>
        <item name="tt_font_medium">@font/noto_sans_semi_bold</item>
        <item name="tt_font_bold">@font/noto_sans_bold</item>
    </style>
</resource>
```

A theme category, like color or font, is represented by an [`IviThemeCategory`](TTIVI_INDIGO_API).
They may contain multiple [`IviThemeComponent`](TTIVI_INDIGO_API)s. For example, a font
[`IviThemeCategory`](TTIVI_INDIGO_API) can have several font 
[`IviThemeComponent`](TTIVI_INDIGO_API)s, each for a different font.

An [`IviTheme`](TTIVI_INDIGO_API) is a collection of [`IviThemeComponent`](TTIVI_INDIGO_API)s.

A [`TtiviThemeCategoryPreset`](TTIVI_INDIGO_API) is a pre-defined set of
[`IviThemeCategory`](TTIVI_INDIGO_API)s.

An [`IviTheme`](TTIVI_INDIGO_API) must contain at least one [`IviThemeComponent`](TTIVI_INDIGO_API) 
for each [`IviThemeCategory`](TTIVI_INDIGO_API) listed in the
[`TtiviThemeCategoryPreset`](TTIVI_INDIGO_API). The [`ThemingService`](TTIVI_INDIGO_API) has the
responsibility to check this.

![Customization class diagram](images/theming-customization-class-diagram.svg)

First you need to define styleable attributes for a category. Next you can define a style for it,
which will be used by an [`IviThemeComponent`](TTIVI_INDIGO_API) later.

Let's use an example to demonstrate the creation of a custom theme. TomTom IndiGO extends color 
attributes like `tt_surface_content_color_emphasis_high` which are defined in `stock_theme`. It 
is defined like this:

```xml
<resources>
    <declare-styleable name="TtThemeCategoryColors">
        <attr name="tt_surface_content_color_emphasis_high" format="color" />
    </declare-styleable>
</resources>
```

And there is a default value for that color attribute in `stock_theme` as well:

```xml
<resources>
    <style name="TtThemeColorLight">
        <item name="tt_surface_content_color_emphasis_high">#2E3841</item>
    </style>
</resources>
```

The TomTom IndiGO style `TtiviThemeColorStock` inherits from the style `TtThemeColorLight` and 
there you can define the value you want for your theme:

```xml
<resources>
    <style name="TtiviThemeColorStock" parent="TtThemeColorLight">
        <item name="tt_surface_content_color_emphasis_high">#A7B290</item>
    </style>
</resources>
```

__Note:__ You can have multiple styles for the same theme category. For example, it is common to
provide light and dark modes as user options. So you can define a light and a dark color theme, and
let the user choose.

You can define a different value for the same attribute in another theme:

```xml
<resources>
    <style name="TtiviThemeColorDark" parent="TtThemeColorDark">
        <item name="tt_surface_content_color_emphasis_high">#FFFFFF</item>
    </style>
</resources>
```

In code, the color category is defined as:

```kotlin
enum class TtiviThemeCategoryPreset(val category: IviThemeCategory) {
    COLOR(IviThemeCategory("COLOR", StaticStringResolver("Color")))
}
```

Then the corresponding [`IviThemeComponent`](TTIVI_INDIGO_API)s are created and provided by a
[`ThemeComponentProviderService`](TTIVI_INDIGO_API):

```kotlin
class StockThemeComponentProviderService(
    iviServiceHostContext: IviServiceHostContext,
    serviceIdProvider: IviDiscoverableServiceIdProvider,
) : ThemeComponentProviderServiceBase(iviServiceHostContext, serviceIdProvider) {
    
    init {
        availableThemeComponents = listOf(
            with(TtiviThemeCategoryPreset.COLOR) {
                IviThemeComponent("Id", category, R.style.TtiviThemeColorStock, "Stock")
            },
            // Adding additional theme category flavors.
            with(TtiviThemeCategoryPreset.COLOR) {
                "Dark".let {
                    IviThemeComponent("Id", category, R.style.TtiviThemeColorDark, it)
                }
            }
        )
    }
}
```

Finally an [`IviTheme`](TTIVI_INDIGO_API) is created from the
[`ThemeComponentProviderService`](TTIVI_INDIGO_API):

```kotlin
class StockThemingService(iviServiceHostContext: IviServiceHostContext) :
    ThemingServiceBase(iviServiceHostContext) {
        override fun onCreate() {
            super.onCreate()
            activeTheme = IviTheme(themeComponentProviderService.availableThemeComponents)
        }
}
```

If you want to slightly change the look, then you can change individual attribute values. If
you need to change the look dramatically, then you can create your own theme categories and
attributes and provide your own [ThemeComponentProviderService](TTIVI_INDIGO_API) to the
`ThemeService`.

