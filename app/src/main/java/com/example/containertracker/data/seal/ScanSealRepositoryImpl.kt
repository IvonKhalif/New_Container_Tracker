package com.example.containertracker.data.seal

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.domain.seal.ScanSealRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ScanSealRepositoryImpl(private val service: ScanSealService) : ScanSealRepository {
    override suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode, containerCode, flag)
    }

    override suspend fun saveSeal(request: SaveSealRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveSeal(request)
    }
}