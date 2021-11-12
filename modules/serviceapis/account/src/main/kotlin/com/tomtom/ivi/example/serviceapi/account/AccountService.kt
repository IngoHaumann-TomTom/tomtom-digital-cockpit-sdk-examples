/*
 * Copyright © 2020 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.tomtom.ivi.example.serviceapi.account

import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.platform.framework.api.common.uid.Uid
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.mirrormap.MirrorableMap
import com.tomtom.ivi.platform.framework.api.ipc.iviserviceannotations.IviService
import com.tomtom.ivi.platform.framework.api.ipc.iviserviceannotations.IviServiceFun

/**
 * IVI service to manage an account state.
 */
@IviService(
    serviceId = "com.tomtom.ivi.example.service.account"
)
interface AccountService {
    /**
     * Indicates which account is currently active.
     * `null` if no account is logged in.
     */
    val activeAccount: Account?

    /**
     * The collection of accounts that have logged in at least once since application started.
     */
    val loggedInAccounts: MirrorableMap<Uid<Account>, Account>

    /**
     * Tries to log an user in under [username] with [password].
     * Returns `true` if the user is logged in successfully, `false` otherwise.
     */
    @IviServiceFun
    suspend fun logIn(username: String, password: SensitiveString): Boolean

    /**
     * Logs the currently logged in user out.
     */
    @IviServiceFun
    suspend fun logOut()

    companion object
}
