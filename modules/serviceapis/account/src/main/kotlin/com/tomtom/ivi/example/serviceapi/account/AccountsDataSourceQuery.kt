package com.tomtom.ivi.example.serviceapi.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountsDataSourceQuery(
    val selection: Selection,
    val orderBy: Order
) : Parcelable {

    enum class Selection {
        ALL,
        LOGGED_IN_AT_LEAST_ONCE
    }

    enum class Order {
        USERNAME,
        LAST_LOG_IN_TIME_DESCENDING
    }
}
