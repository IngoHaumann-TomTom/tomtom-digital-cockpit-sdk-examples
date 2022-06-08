/*
 * Copyright © 2022 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.example.ivi.example.telephony.customcontacts

import com.tomtom.ivi.platform.contacts.api.common.model.Contact
import com.tomtom.ivi.platform.contacts.api.common.model.ContactId
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceElement
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceElement.ContactGroup
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceElement.ContactItem
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactGroupOrder
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactGroupOrder.GROUP_ASC
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactGroupOrderBy
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactItemOrder
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactItemOrder.COMPANY_NAME_ASC
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactItemOrder.FAMILY_NAME_ASC
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactItemOrder.FIRST_NAME_ASC
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactItemOrder.PRIMARY_SORT_KEY
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactOrderBy.ContactItemOrderBy
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.All
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.Favorites
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.FindContactByPhoneNumber
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.FindContactsByCompanyName
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.FindContactsByFamilyName
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.FindContactsByFirstName
import com.tomtom.ivi.platform.contacts.api.service.contacts.ContactsDataSourceQuery.ContactSelection.Groups
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.datasource.IviPagingSource
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.datasource.MutableIviDataSource
import com.tomtom.ivi.platform.framework.api.ipc.iviservice.datasource.MutableIviPagingSource

/**
 * Load data from a data set and create a page out of it.
 */
internal class MutableCustomContactsDataSource :
    MutableIviDataSource<ContactsDataSourceElement, ContactsDataSourceQuery>(
        jumpingSupported = true
    ) {

    private val mutableContacts = mutableMapOf<ContactId, Contact>()

    private val contacts: Map<ContactId, Contact> = mutableContacts

    /**
     * Add or update contact page.
     */
    fun addOrUpdateContact(contact: Contact) {
        mutableContacts[contact.contactId] = contact
        invalidateAllPagingSources()
    }

    override fun createPagingSource(query: ContactsDataSourceQuery):
        MutableIviPagingSource<ContactsDataSourceElement> {
        return MutableContactsPagingSource(
            when (query.selection) {
                is All -> {
                    contacts.values.map {
                        ContactItem(it)
                    }
                }
                is Favorites -> {
                    contacts.values.filter {
                        it.favorite
                    }.map {
                        ContactItem(it)
                    }
                }
                is Groups -> {
                    contacts.values.groupBy {
                        it.toFirstLetter()
                    }.map {
                        ContactGroup(it.key.toString(), it.value.size)
                    }
                }
                is FindContactsByFirstName -> {
                    contacts.values.filter {
                        it.givenName.startsWith(
                            (query.selection as FindContactsByFirstName).firstName,
                            true
                        )
                    }.map {
                        ContactItem(it)
                    }
                }

                is FindContactsByFamilyName -> {
                    contacts.values.filter {
                        it.familyName.startsWith(
                            (query.selection as FindContactsByFamilyName).familyName,
                            true
                        )
                    }.map {
                        ContactItem(it)
                    }
                }

                is FindContactsByCompanyName -> {
                    contacts.values.filter {
                        it.companyName.startsWith(
                            (query.selection as FindContactsByCompanyName).companyName,
                            true
                        )
                    }.map {
                        ContactItem(it)
                    }
                }

                is FindContactByPhoneNumber -> {
                    contacts.values.filter {
                        it.phoneNumbers.any { phoneNumber ->
                            (query.selection as? FindContactByPhoneNumber)
                                ?.phoneNumber == phoneNumber.number
                        }
                    }.map {
                        ContactItem(it)
                    }
                }
            }.let { data ->
                when (query.orderBy) {
                    is ContactItemOrderBy -> {
                        return@let sortContactItems(
                            (query.orderBy as ContactItemOrderBy).order,
                            data
                        )
                    }
                    is ContactGroupOrderBy -> {
                        return@let sortContactGroups(
                            (query.orderBy as ContactGroupOrderBy).order,
                            data
                        )
                    }
                    else -> return@let data
                }
            }
        )
    }

    private fun sortContactItems(
        order: ContactItemOrder,
        data: List<ContactsDataSourceElement>
    ): List<ContactsDataSourceElement> {
        when (order) {
            COMPANY_NAME_ASC -> {
                data.sortedBy {
                    (it as? ContactItem)?.contact?.companyName
                }.also {
                    return it
                }
            }
            FAMILY_NAME_ASC -> {
                data.sortedBy {
                    (it as? ContactItem)?.contact?.familyName
                }.also {
                    return it
                }
            }
            FIRST_NAME_ASC -> {
                data.sortedBy {
                    (it as? ContactItem)?.contact?.givenName
                }.also {
                    return it
                }
            }
            PRIMARY_SORT_KEY -> {
                data.sortedBy {
                    (it as? ContactItem)?.contact?.primarySortKey
                }.also {
                    return it
                }
            }
        }
    }

    private fun sortContactGroups(
        order: ContactGroupOrder,
        data: List<ContactsDataSourceElement>
    ): List<ContactsDataSourceElement> {
        when (order) {
            GROUP_ASC -> {
                data.sortedBy {
                    (it as? ContactGroup)?.group
                }.also {
                    return it
                }
            }
            else -> {
                return data
            }
        }
    }

    /**
     * Groups contact by first letter. Contact starting with no letter character are grouped in
     * [OTHER_GROUP_HEADER_SORTING_TAG].
     */
    private fun Contact.toFirstLetter() =
        primarySortKey.first().let { firstCharacter ->
            if (firstCharacter.isLetter()) {
                firstCharacter
            } else {
                OTHER_GROUP_HEADER_SORTING_TAG
            }
        }

    private class MutableContactsPagingSource(val data: List<ContactsDataSourceElement>) :
        MutableIviPagingSource<ContactsDataSourceElement>() {
        override val loadSizeLimit = DATA_SOURCE_MAX_PAGE_SIZE

        override suspend fun loadWithLoadSizeLimited(
            loadParams: IviPagingSource.LoadParams
        ): IviPagingSource.LoadResult<ContactsDataSourceElement> {
            return when (loadParams) {
                is IviPagingSource.LoadParams.Refresh,
                is IviPagingSource.LoadParams.Append -> {
                    val dataIndex = minOf(loadParams.dataIndex, data.size)
                    createPage(
                        dataIndex = dataIndex,
                        pageSize = minOf(loadParams.loadSize, data.size - dataIndex),
                        placeholdersEnabled = loadParams.placeholdersEnabled
                    )
                }
                is IviPagingSource.LoadParams.Prepend -> {
                    val size = minOf(loadParams.loadSize, loadParams.dataIndex, data.size)
                    val dataIndex = loadParams.dataIndex - size
                    createPage(
                        dataIndex = dataIndex,
                        pageSize = size,
                        placeholdersEnabled = loadParams.placeholdersEnabled
                    )
                }
            }
        }

        private fun createPage(
            dataIndex: Int,
            pageSize: Int,
            placeholdersEnabled: Boolean
        ): IviPagingSource.LoadResult.Page<ContactsDataSourceElement> {
            return IviPagingSource.LoadResult.Page(
                dataIndex = dataIndex,
                data = data.subList(dataIndex, dataIndex + pageSize),
                itemsBefore = dataIndex.takeIf { placeholdersEnabled },
                itemsAfter = (data.size - dataIndex - pageSize).takeIf { placeholdersEnabled }
            )
        }
    }

    private companion object {
        // For optimal usage of data source, the maximum page should fit 500kb, including contacts
        // avatars. Setting page size to 50 ensures that page size remains under the optimal
        // recommended size.
        private const val DATA_SOURCE_MAX_PAGE_SIZE: Int = 50

        private const val OTHER_GROUP_HEADER_SORTING_TAG = Char.MAX_VALUE
    }
}
