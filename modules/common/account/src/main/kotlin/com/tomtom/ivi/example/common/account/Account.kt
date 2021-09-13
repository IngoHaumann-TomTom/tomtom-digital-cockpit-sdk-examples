/*
 * Copyright © 2020 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, then you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
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