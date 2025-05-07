package com.example.containertracker.data.marking

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.domain.marking.ScanMarkingRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ScanMarkingRepositoryImpl(private val service: ScanMarkingService) : ScanMarkingRepository {
    override suspend fun getContainer(
        qrCode: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode)
    }

    override suspend fun saveMarking(request: SaveMarkingRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveMarking(request)
    }

}