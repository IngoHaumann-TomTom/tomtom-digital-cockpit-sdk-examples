# Companion Messaging Service Api

This is the API of the companion messaging service.

## Package: com.tomtom.ivi.sdk.communications.messagingservice

### Types

| Type                                                    | Description                                       |
| ------------------------------------------------------- | ------------------------------------------------- |
| [ConversationId](#type:-conversationid)                 | A universally unique ID for conversations.        |
| [MessageId](#type:-messageid)                           | A universally unique ID for messages.             |
| [MessageState](#enum:-messagestate)                     | The state of a message.                           |
| [Message](#type:-message)                               | A message.                                        |
| [ConversationContact](#type:-conversationcontact)       | A contact.                                        |
| [ConversationCapability](#enum:-conversationcapability) | The capability of a conversation.                 |
| [Conversation](#type:-conversation)                     | A conversation.                                   |
| [SendMessageResult](#type:-sendmessageresult)           | Result for [sendMessage](#function:-sendmessage). |

### Properties

| Field                                                                                   | Description               |
| --------------------------------------------------------------------------------------- | ------------------------- |
| [conversations](#property:-conversations): LiveData<Map<ConversationId, Conversation?>> | All known conversations.  |
| [messages](#property:-messages): LiveData<Map<MessageId, Message?>>                     | All known messages.       |

### Service: MessageService

| Field                                         | Description                                                                               |
| --------------------------------------------- | ----------------------------------------------------------------------------------------- |
| [sendMessage(message: Message)](#sendMessage) | Sends the specified [message]. Returns the resulting [MessageState](#enum:-messagestate). |

### Type: ConversationId

| Field      | Description                               |
| ---------- | ----------------------------------------- |
| uuid: Uuid | Universally unique ID for a Conversation. |

### Type: MessageId

| Field       | Description                          |
| ----------- | ------------------------------------ |
| value: Uuid | Universally unique ID for a Message. |

### Enum: MessageState

The state of a message.

| Field              | Description                                            |
| ------------------ | ------------------------------------------------------ |
| INCOMING_UNREAD    | Incoming message that is unread.                       |
| INCOMING_READ      | Incoming message that is read.                         |
| OUTGOING_QUEUED    | Outgoing message that is queued.                       |
| OUTGOING_SENDING   | Outgoing message that is in the process of being sent. |
| OUTGOING_SENT      | Outgoing message that has been sent.                   |
| OUTGOING_DELIVERED | Outgoing message that has been delivered.              |
| OUTGOING_READ      | Outgoing message that has been read by the recipient.  |
| OUTGOING_FAILED    | Outgoing message that could not be delivered.          |

### Type: Message

Represents a message.

| Field                                                     | Description                                                                                                                                                                            |
| --------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id: [MessageId](#type:-messageid)                         | The universally unique ID of the message.                                                                                                                                              |
| conversationId: [ConversationId](#type:-conversationid)   | The universally unique ID of the conversation to which the message belongs.                                                                                                            |
| author: [ConversationContact](#type:-conversationcontact) | The author of the message. Never `null` for incoming messaging, always `null` for outgoing messages.                                                                                   |
| state: [MessageState](#enum-messagestate)                 | The state of the message.                                                                                                                                                              |
| timestamp: Long                                           | The UTC timestamp in milliseconds of the message. This will be set to the time the message was sent for outgoing messages and the time the message was received for incoming messages. |
| contentText: String                                       | The textual content of the message.                                                                                                                                                    |

### Type: ConversationContact

The contact information for remote parties on a specific messaging application.

| Field                | Description                                                |
| -------------------- | ---------------------------------------------------------  |
| displayName: String? | The display name of the contact. Set to `null` if unknown. |
| phoneNumber: String? | The phone number of the contact. Set to `null` if unknown. |

### Enum: ConversationCapability

| Field                | Description                                                                   |
| -------------------- | ----------------------------------------------------------------------------- |
| CAN_REPLY_USING_TEXT | Indicates that it is possible to reply to a conversation using text.          |
| CAN_CALL_USING_VOICE | Indicates that it is possible to make a voice call to a conversation contact. |

### Type: Conversation

Represents a conversation between the user and one or more contacts.

| Field                                                             | Description                                                                                                                                                                                                                                   |
| ----------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id: [ConversationId](#type:-conversationid)                       | The universally unique ID of the conversation.                                                                                                                                                                                                |
| contacts: List<[ConversationContact](#type:-conversationcontact)> | The contacts whose messages are inside this conversation. This is a set of all contacts that participate in this conversation except for the current user. Note that when this is not a group conversation there will be exactly one contact. |
| capabilities: List<Int>                                           | The capabilities for this conversation. Note that it is possible for capabilities to change during the lifetime of a conversation.                                                                                                            |
| applicationDisplayName: String                                    | The display name of the messaging application to which the conversation belongs.                                                                                                                                                              |

### Type: SendMessageResult

The result of a call to [sendMessage]. Returned values have to be messages, so this cannot be
the [MessageState] enum directly.

| Field                                      | Description        |
| ------------------------------------------ | ------------------ |
| state: [MessageState](#type:-messagestate) | The message state. |

### Property: conversations

All known conversations.

```kotlin
val conversations: LiveData<Map<ConversationId, Conversation?>>
```

### Property: messages

All known messages.

```kotlin
public val messages: LiveData<Map<MessageId, Message?>>
```

### Function: sendMessage

Sends the specified [message]. The [message] should specify the conversation ID as well as the content of the message.
The [message] will be added as outgoing to the [messages] property. Any state changes to the message (e.g. it was
sent or it failed to send) will be updated in the [messages] property.

```kotlin
  public suspend fun sendMessage(message: Message): SendMessageResult
```

## Copyright

Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.

This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used for
internal evaluation purposes or commercial use strictly subject to separate licensee agreement
between you and TomTom. If you are the licensee, you are only permitted to use this Software in
accordance with the terms of your license agreement. If you are not the licensee then you are not
authorised to use this software in any manner and should immediately return it to TomTom N.V.