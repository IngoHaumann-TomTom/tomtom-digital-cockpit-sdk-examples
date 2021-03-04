package com.tomtom.ivi.example.serviceapi.account

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
     * The name of the user that is currently logged in.
     * `null` if no user is logged in.
     */
    val username: String?

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