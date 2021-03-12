package com.tomtom.ivi.example.product.exampleapp

import com.tomtom.ivi.api.defaults.application.DefaultApplication
import com.tomtom.ivi.api.framework.iviservice.IviServerId
import com.tomtom.ivi.api.common.internal.IviInternal
import com.tomtom.ivi.example.serviceapi.account.AccountServiceBinderConnection

//TODO(IVI-2756): Remove OptIn when service registration is internal.
@OptIn(IviInternal::class)
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