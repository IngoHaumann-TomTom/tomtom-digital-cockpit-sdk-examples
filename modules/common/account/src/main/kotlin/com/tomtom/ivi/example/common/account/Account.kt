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

package com.tomtom.ivi.example.common.account

import android.os.Parcelable
import com.tomtom.ivi.api.common.uid.Uid
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    /**
     * Unique ID for the account.
     */
    val accountUid: Uid<Account>,

    /**
     * A string representing the name of the account.
     */
    val username: String
) : Parcelable {
    constructor(username: String) : this(Uid.new(), username)
}