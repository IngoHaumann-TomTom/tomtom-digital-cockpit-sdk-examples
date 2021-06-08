/*
 * Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.ivi.example.serviceapi.accountsettings

import com.tomtom.ivi.api.common.experimental.IviExperimental
import com.tomtom.ivi.api.framework.iviserviceannotations.IviService
import com.tomtom.ivi.api.framework.iviserviceannotations.IviSetting
import com.tomtom.ivi.api.framework.iviserviceannotations.IviSettingAccessMode
import com.tomtom.ivi.example.common.account.Account

@IviService(
    serviceId = "com.tomtom.ivi.example.service.accountsettings"
)
@OptIn(IviExperimental::class)
interface AccountSettingsService {
    @IviSetting(accessMode = IviSettingAccessMode.READ_WRITE)
    val activeAccount: Account?

    /**
     * The timestamp of the moment of logging in seconds since Epoch.
     * It is 0L if there is no logged in user.
     * It is updated when `activeAccount` is changed.
     */
    @IviSetting(accessMode = IviSettingAccessMode.READ_ONLY)
    val loginTimestamp: Long

    @IviSetting(accessMode = IviSettingAccessMode.READ_ONLY)
    val onlineLoginValidPeriodInDays: Long

    companion object
}