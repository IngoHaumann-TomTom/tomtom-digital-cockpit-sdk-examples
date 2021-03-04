package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.core.framework.iviservice.IviServerContext
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBase

class StockAccountService(iviServerContext: IviServerContext) :
    AccountServiceBase(iviServerContext) {

    override fun onCreate() {
        super.onCreate()
        username = null
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun logIn(username: String, password: String): Boolean =
        if (isValidUsername(username) && isValidPassword(password)) {
            this.username = username
            true
        } else {
            false
        }

    override suspend fun logOut() {
        username = null
    }

    companion object {
        private fun isValidUsername(value: String) = value.trim().length > 1
        private fun isValidPassword(value: String) = value.trim().length > 2
    }
}