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

package com.tomtom.ivi.example.frontend.account.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.ivi.example.serviceapi.account.AccountService
import com.tomtom.ivi.example.serviceapi.account.AccountsDataSourceQuery
import com.tomtom.ivi.example.serviceapi.account.SensitiveString
import com.tomtom.ivi.example.serviceapi.account.createApi
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.datasource.IviDataSource
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.datasource.first
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.datasource.mapQuery
import com.tomtom.ivi.platform.framework.api.ipc.iviserviceandroidpaging.mapPagingData
import com.tomtom.ivi.platform.frontend.api.common.frontend.viewmodels.FrontendViewModel
import com.tomtom.tools.android.api.livedata.allTrue
import com.tomtom.tools.android.api.livedata.valueUpToDate
import kotlinx.coroutines.flow.Flow

internal class AccountLoginViewModel(panel: AccountLoginPanel) :
    FrontendViewModel<AccountLoginPanel>(panel) {

    private val accountServiceApi =
        AccountService.createApi(this, frontendContext.iviServiceProvider)

    val username = MutableLiveData("")
    val password = MutableLiveData("")

    val isLoginEnabled = allTrue(
        accountServiceApi.serviceAvailable,
        username.map { it.isNotBlank() },
        password.map { it.isNotBlank() }
    )

    /**
     * Converts an [IviDataSource] [LiveData] to an [Account] [LiveData], the value of which is set
     * to the first item of the query result set.
     */
    @Suppress("unused")
    val lastLogin: LiveData<Account> =
        accountServiceApi.accounts.mapQuery(lastLoginQuery).first()

    /**
     * Converts an [IviDataSource] [LiveData] to an [Flow] of [PagingData]. This
     * flow can be bound to an `RecyclerView`. See Android Paging library for details.
     */
    @Suppress("unused")
    val allAccountsPagingDataFlow: Flow<PagingData<Account>> = accountServiceApi.accounts
        .mapPagingData(pagingConfig, allAccountsQuery, this)

    fun onLoginClick() {
        isLoginEnabled.valueUpToDate?.takeIf { it }?.let {
            val username = username.value ?: return
            val password = password.value ?: return
            accountServiceApi.logInAsync(username, SensitiveString(password))
        }
    }

    companion object {
        @VisibleForTesting
        internal val lastLoginQuery = AccountsDataSourceQuery(
            selection = AccountsDataSourceQuery.Selection.LOGGED_IN_AT_LEAST_ONCE,
            orderBy = AccountsDataSourceQuery.Order.LAST_LOG_IN_TIME_DESCENDING
        )

        @VisibleForTesting
        internal val allAccountsQuery = AccountsDataSourceQuery(
            selection = AccountsDataSourceQuery.Selection.ALL,
            orderBy = AccountsDataSourceQuery.Order.USERNAME
        )

        private val pagingConfig = PagingConfig(
            pageSize = 10
        )
    }
}
