package com.pustovit.sbp_app_detector.impl

import com.pustovit.sbp_app_detector.SbpAppDetector
import com.pustovit.sbp_app_detector.model.SbpBankDto
import com.pustovit.sbp_app_detector.network.SbpConnection

internal class RemoteDataSourceImpl(private val sbpConnection: SbpConnection):SbpAppDetector.RemoteDataSource {

    override suspend fun getSbpBanks(): List<SbpBankDto> {
        return sbpConnection.getSbpBanks()
    }
}