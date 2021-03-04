package com.tomtom.ivi.example.service.account

import com.tomtom.ivi.core.framework.iviservice.IviServerBase
import com.tomtom.ivi.core.framework.iviservice.IviServerContext
import com.tomtom.ivi.core.framework.iviservice.SimpleIviServiceServer
import com.tomtom.ivi.core.framework.iviservicemanager.IviAndroidServer

class AndroidServer : IviAndroidServer() {
    override fun createIviServer(iviServerContext: IviServerContext): IviServerBase =
        SimpleIviServiceServer(setOf(StockAccountService(iviServerContext)))
}