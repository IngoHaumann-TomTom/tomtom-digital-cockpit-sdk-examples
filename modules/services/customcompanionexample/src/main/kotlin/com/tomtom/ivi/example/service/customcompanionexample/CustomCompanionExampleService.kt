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

package com.tomtom.ivi.example.service.customcompanionexample

import com.tomtom.ivi.api.framework.iviservice.IviServiceHostContext
import com.tomtom.ivi.api.framework.iviservice.mirrormap.MutableMirrorableMap
import com.tomtom.ivi.example.serviceapi.companionexample.CompanionExampleServiceBase
import com.tomtom.ivi.sdk.communications.client.CommunicationsClient
import com.tomtom.ivi.sdk.communications.client.CommunicationsClientContext
import com.tomtom.ivi.sdk.communications.client.CommunicationsClientListener
import com.tomtom.ivi.sdk.communications.common.ServiceProviderId
import com.tomtom.ivi.sdk.communications.exampleservice.ExampleId
import com.tomtom.ivi.sdk.communications.exampleservice.ExampleMessageRequest
import com.tomtom.ivi.sdk.communications.exampleservice.ExampleService
import com.tomtom.ivi.sdk.communications.serviceframework.CommunicationsServiceBase
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * A custom companion service that illustrates how to proxy the communications sdk ExampleService
 * over an Ivi service.
 */
class CustomCompanionExampleService(iviServiceHostContext: IviServiceHostContext) :
    CompanionExampleServiceBase(iviServiceHostContext) {

    /**
     * Map of all connected companion apps. Every connected companion app provides its own proxy.
     */
    private val companionAppProxies = mutableMapOf<ServiceProviderId, ExampleService>()

    private val mutableTestMap = MutableMirrorableMap<Int, String>().also { testMap = it }

    /**
     * The communications client context that indicates what service we want to connect to (in this
     * case the ExampleService).
     */
    // TODO(IVI-4703): Remove [OptIn] when [CommunicationsClientContext] allows it.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val communicationsClientContext = CommunicationsClientContext(
        iviServiceHostContext.context,
        this,
        ExampleService.ClientFactory()
    )

    private val communicationsServiceClientListener = object : CommunicationsClientListener {
        /**
         * This is called when a companion app that provides the ExampleService is connected .
         * Each service provided by a companion app has a unique service provider ID.
         */
        override fun onServiceConnected(
            serviceProviderUuid: ServiceProviderId,
            client: CommunicationsServiceBase
        ) {
            companionAppProxies[serviceProviderUuid] = client as ExampleService
            client.testLiveDataProperty.observe(this@CustomCompanionExampleService) {
                testProperty = it?.stuff ?: ""
            }
            client.testLiveDataMapProperty.observe(this@CustomCompanionExampleService) { sourceMap ->
                mutableTestMap.keys.retainAll(sourceMap.keys.map { it.value })
                mutableTestMap.putAll(sourceMap.map { it.key.value to (it.value?.stuff ?: "") })
            }
        }

        /**
         * This is called when a connection is lost to a previously connected service.
         */
        override fun onServiceDisconnected(serviceProviderUuid: ServiceProviderId) {
            companionAppProxies.remove(serviceProviderUuid)
        }
    }

    /**
     * This starts listening to available [ExampleService] services on any connected companion app.
     */
    // TODO(IVI-4703): Remove [OptIn] when [CommunicationsClient] allows it.
    @OptIn(ExperimentalCoroutinesApi::class)
    val communicationsClient = CommunicationsClient(
        communicationsClientContext,
        communicationsServiceClientListener
    )

    init {
        testProperty = "default value"
        serviceReady = true
    }

    override suspend fun testFunctionCall(bar: String): String? {
        /**
         * Proxy a service api function call to the first connected companion app.
         */
        return companionAppProxies.values.firstOrNull()?.testFunctionCall(
            ExampleMessageRequest.newBuilder()
                .setBar(bar)
                .setId(ExampleId.getDefaultInstance())
                .build()
        )?.stuff
    }
}
