package com.tomtom.ivi.example.service.account

import androidx.lifecycle.MutableLiveData
import com.tomtom.ivi.api.framework.iviservice.IviServerContext
import com.tomtom.ivi.api.framework.iviservice.mirrormap.MutableMirrorableMap
import com.tomtom.ivi.example.serviceapi.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountId
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBase

class StockAccountService(iviServerContext: IviServerContext) :
        AccountServiceBase(iviServerContext) {

    private val mutableAccounts =
            MutableMirrorableMap<AccountId, Account>()

    override fun onCreate() {
        super.onCreate()

        accounts = mutableAccounts
    }

    override fun onRequiredPropertiesInitialized() {
        serviceReady = true
    }

    override suspend fun logIn(username: String, password: String): Boolean =
        if (isValidUsername(username) && isValidPassword(password)) {
            val accountId = AccountId(username)

            if (!mutableAccounts.contains(accountId)) {
                val account = Account(accountId, username)
                mutableAccounts[accountId] = account
            }

            activeAccount = mutableAccounts[accountId]

            true
        } else {
            false
        }

    override suspend fun logOut() {
        activeAccount = null
    }

    companion object {
        private fun isValidUsername(value: String) = value.trim().length > 1
        private fun isValidPassword(value: String) = value.trim().length > 2
    }
}