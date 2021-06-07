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

package com.tomtom.ivi.example.service.customrecentcalls

import com.tomtom.ivi.api.framework.iviservice.IviServiceHostBuilder
import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
import com.tomtom.ivi.api.framework.iviservice.SimpleIviServiceHost

/**
 * The builder class for the `CustomRecentCallsServiceHost`.
 *
 * It must contain at least the empty companion object, which will be extended by the IVI service
 * framework. Kotlin does not allow extending the companion object if it has not been declared.
 */
class CustomRecentCallsServiceHostBuilder : IviServiceHostBuilder() {

    override fun build(iviServiceHostContext: IviServiceHostContext) =
        SimpleIviServiceHost(
            iviServiceHostContext,
            setOf(CustomRecentCallsService(iviServiceHostContext))
        )

    companion object
}
