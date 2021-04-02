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


package com.tomtom.ivi.example.product.exampleapp

import com.tomtom.ivi.api.common.internal.IviInternal
import com.tomtom.ivi.api.defaults.application.DefaultApplication
import com.tomtom.ivi.api.framework.iviservice.IviServerId
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBinderConnection

//TODO(IVI-2756): Remove OptIn when service registration is internal.
@OptIn(IviInternal::class)
@Suppress("unused")
class ExampleApplication : DefaultApplication() {
    override fun registerBinderServers() {
        super.registerBinderServers()

        with(serviceManager) {
            registerServerManager(
                createBinderServerManager(
                    IviServerId("com.tomtom.ivi.example.service.account.AndroidServer"),
                    AccountServiceBinderConnection.createRegistration()
                )
            )
        }
    }
}