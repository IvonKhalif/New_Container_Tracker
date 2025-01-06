package com.example.containertracker.data.flexi

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.domain.flexi.ScanFlexiRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ScanFlexiRepositoryImpl(private val service: ScanFlexiService) : ScanFlexiRepository {
    override suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode, containerCode, flag)
    }

    override suspend fun saveFlexi(request: SaveFlexiRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveFlexi(request)
    }
}