# Companion Calendar Service Api

This is the API of the companion calendar service.

## Package: com.tomtom.ivi.sdk.communications.calendarservice

### Types

| Type                                            | Description                                                                        |
| ----------------------------------------------- | ---------------------------------------------------------------------------------- |
| [EventId](#type:-eventid)                       | Uniquely identifies a calendar event.                                              |
| [Event](#type:-event)                           | Represents an event entry in a calendar.                                           |
| [UpcomingEventQuery](#type:-upcomingeventquery) | Query message used to specify how to filter a search for upcoming calendar events. |

### CalendarService

#### Properties

| Field                                                                       | Description                |
| --------------------------------------------------------------------------- | -------------------------- |
| [upcomingEvents](#property:-upcomingevents): LiveData<Map<EventId, Event?>> | Upcoming calendar events.  |

### Type: EventId

Uniquely identifies a calendar event.

| Field      | Description                         |
| ---------- | ----------------------------------- |
| uuid: Uuid | Universally unique ID for an Event. |

### Type: Event

Represents an event entry in a calendar.

| Field                                                     | Description                                                                                                                           |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| id: [EventId](#type:-eventid)                             | A unique identifier of the event.                                                                                                     |
| organizer: String                                         | The organizer of the event. This can be, for example, the host of the meeting or the person that sent the event invitation.           |
| title: String                                             | The title of the event.                                                                                                               |
| location: String                                          | The location that the event will take place at.                                                                                       |
| description: String                                       | Details about the event. This is typically free-form text that the originator of the event will have entered when creating the event. |
| startTimeUtcMillis: Long                                  | The start time of the event in milliseconds since the Unix Epoch.                                                                     |
| endTimeUtcMillis: Long                                    | The end time of the event in milliseconds since the Unix Epoch.                                                                       |
| availability: [Availability](#enum:-availability)         | The availability state of the owner of the calendar with respect to this event.                                                       |

### Enum: Availability

Represents the availability state of the owner of the calendar with respect to a calendar event.

| Field     | Description                                                                                                                                                                                                               |
| --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| BUSY      | The owner of this calendar is marked as being busy during this event. This usually indicates that they are attending this event and hence is not available to participate in another event taking place at the same time. |
| FREE      | The owner of this calendar is marked as being free during this event.                                                                                                                                                     |
| TENTATIVE | The owner of this calendar is marked as tentative during this event. They have either not RSVP'd to the event or have indicated that they may not definitely attend the event.                                            |
| UNKNOWN   | The availability state of the owner is not known.                                                                                                                                                                         |

### Type: UpcomingEventQuery

Query message used to specify how to filter a search for upcoming calendar events.

| Field                       | Description                                                                          |
| --------------------------- | ------------------------------------------------------------------------------------ |
| startsBeforeUtcMillis: Long | Query for events that start before this time (in milliseconds since the Unix Epoch). |

### Property: upcomingEvents

Upcoming calendar events.

```kotlin
val upcomingEvents: LiveData<Map<EventId, Event?>>
```

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.