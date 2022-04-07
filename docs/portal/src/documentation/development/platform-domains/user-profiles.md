---
title: User Profiles
layout: default
---

It is important to allow users to customize their usage of an infotainment system. TomTom IndiGO 
allows the creation of a personalized user experience by linking settings and preferences to
an active user profile. It is possible to have user profile specific settings with different 
configuration values depending on the current user using the platform. This can be convenient, 
for instance, when a user wants to use a UI with a dark theme and another user prefers to use a 
light theme.

## Overview

Tomtom IndiGO provides the [`UserProfileManagementService`](TTIVI_INDIGO_API) API to allow services 
and frontends to manage user profiles. A user profile can be created with 
[`UserProfileManagementService`](TTIVI_INDIGO_API)`.createUserProfile`, it will be persistently 
stored, and loaded after the service restarts. The user profile information may be updated with
[`UserProfileManagementService`](TTIVI_INDIGO_API).`updateUserProfile` or deleted with 
[`UserProfileManagementService`](TTIVI_INDIGO_API)`.deleteUserProfile`. One of the created user 
profiles must be set as active with 
[`UserProfileManagementService`](TTIVI_INDIGO_API)`.activateUserProfile`. When a user is set as 
active, all setting values with user [`SettingScope`](TTIVI_INDIGO_API) will be reloaded. The active 
user profile cannot be deleted. The service can be used to retrieve the user profiles information as 
well as the ID of the active user.
