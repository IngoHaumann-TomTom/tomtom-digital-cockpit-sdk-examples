package com.tomtom.ivi.example.frontend.account

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tomtom.ivi.example.common.account.Account
import com.tomtom.tools.android.testing.mock.niceMockk

internal class AccountTestPagingDataAdapter : PagingDataAdapter<Account, RecyclerView.ViewHolder>(
    DiffCallback()
) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Nothing here.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        niceMockk()
}

private class DiffCallback : DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean =
        oldItem.accountUid == newItem.accountUid

    override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean =
        oldItem == newItem
}
