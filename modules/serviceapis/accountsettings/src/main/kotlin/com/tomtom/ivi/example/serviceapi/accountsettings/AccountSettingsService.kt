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