---
title: Use Quick Reply Settings
---

The IVI platform comes with a [`MessagingAppSettingsService`](TTIVI_INDIGO_API) that provides
settings for quick replies that can be exposed to end-users. The current settings include:

- A boolean that indicates whether the quick replies of a message notification should include
  _default quick replies_.
- A boolean that indicates whether the quick replies of a message notification should include
- _custom quick replies_.
- A list of _custom quick replies_ defined by the user.

## Reading a setting

To read a setting, you first need to create a [`MessagingAppSettingsService`](TTIVI_INDIGO_API):

```kotlin
private val messagingSettingsService =
    MessagingAppSettingsService.createApi(this, frontendContext.iviServiceProvider)
```

Now you can get to the value you want to read:

```kotlin
val quickRepliesList = messagingSettingsService.quickReplies.value
val showCustomQuickReplies = messagingSettingsService.includeCustomQuickReplies.value
val showDefaultQuickReplies = messagingSettingsService.includeDefaultQuickReplies.value
```

## Updating a setting

To update a setting, you first need to create a [`MessagingAppSettingsService`](TTIVI_INDIGO_API):

```kotlin
private val messagingSettingsService =
    MessagingAppSettingsService.createApi(this, frontendContext.iviServiceProvider)
```

Now you can update the value stored in the settings:

```kotlin
messagingSettingsService.updateQuickRepliesAsync(quickRepliesList)
messagingSettingsService.updateIncludeDefaultQuickRepliesAsync(value)
messagingSettingsService.updateIncludeCustomQuickRepliesAsync(value)
```

Here, `quickRepliesList` must be of type `List<String>` and `value` must be of type `Boolean`. Note
that updating a value overrides the value currently stored in the setting.
