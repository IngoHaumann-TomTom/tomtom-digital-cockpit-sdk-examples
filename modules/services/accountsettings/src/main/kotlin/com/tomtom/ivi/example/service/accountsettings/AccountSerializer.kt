/*
 * Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.ivi.example.service.accountsettings

import com.tomtom.ivi.api.common.uid.Uid
import com.tomtom.ivi.example.common.account.Account
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@SerialName("UserProfile")
private class AccountSurrogate(val accountUid: String, val username: String)

internal object AccountSerializer : KSerializer<Account> {
    override val descriptor: SerialDescriptor = AccountSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Account =
        decoder
            .decodeSerializableValue(AccountSurrogate.serializer())
            .let { surrogate ->
                Account(Uid.fromString(surrogate.accountUid), surrogate.username)
            }

    override fun serialize(encoder: Encoder, value: Account) =
        AccountSurrogate(value.accountUid.toString(), value.username)
            .let { surrogate ->
                encoder.encodeSerializableValue(AccountSurrogate.serializer(), surrogate)
            }
}