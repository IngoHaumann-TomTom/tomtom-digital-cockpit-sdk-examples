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

package com.tomtom.ivi.example.serviceapi.companionexample

import com.tomtom.ivi.api.framework.iviservice.mirrormap.MirrorableMap
import com.tomtom.ivi.api.framework.iviserviceannotations.IviService
import com.tomtom.ivi.api.framework.iviserviceannotations.IviServiceFun

/**
 * IVI service api for the companion service. This is used to illustrate how to proxy a companion
 * app service over a service api.
 */
@IviService(
    serviceId = "com.tomtom.ivi.example.service.companionexample"
)
interface CompanionExampleService {
    val testProperty: String
    val testMap: MirrorableMap<Int, String>

    @IviServiceFun
    suspend fun testFunctionCall(bar: String) : String?

    companion object
}
