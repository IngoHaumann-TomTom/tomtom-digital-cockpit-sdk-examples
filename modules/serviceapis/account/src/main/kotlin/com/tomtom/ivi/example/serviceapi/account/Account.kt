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

package com.tomtom.ivi.example.serviceapi.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountId(val value: String) : Parcelable {
    override fun toString() = value
}

@Parcelize
data class Account (
    /**
     * Unique ID for the account.
     */
    val accountId: AccountId,

    /**
     * A string representing the name of the account.
     */
    val username: String
): Parcelable