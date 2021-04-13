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

import com.tomtom.ivi.example.common.account.Account
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class AccountSerializerTest {
    @Test
    fun `serialize and deserialize an account`() {
        val account = Account("username")

        val serialized = Json.encodeToString(AccountSerializer, account)
        val deserialized = Json.decodeFromString(AccountSerializer, serialized)

        assertEquals(account, deserialized)
    }
}