package com.tomtom.ivi.example.serviceapi.account

import com.tomtom.ivi.api.framework.iviservice.mirrormap.MirrorableMap
import com.tomtom.ivi.core.common.iviserviceannotations.IviService
import com.tomtom.ivi.core.common.iviserviceannotations.IviServiceFun

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
    val accounts: MirrorableMap<AccountId, Account>

    /**
     * Tries to log an user in under [username] with [password].
     * Returns `true` if the user is logged in successfully, `false` otherwise.
     */
    @IviServiceFun
    suspend fun logIn(username: String, password: String): Boolean

    /**
     * Logs the currently logged in user out.
     */
    @IviServiceFun
    suspend fun logOut()

    companion object
}