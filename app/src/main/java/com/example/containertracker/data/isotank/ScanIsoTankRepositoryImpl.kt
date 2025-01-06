package com.example.containertracker.data.isotank

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.isotank.requests.SaveIsoTankRequest
import com.example.containertracker.domain.isotank.ScanIsoTankRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ScanIsoTankRepositoryImpl(private val service: ScanIsoTankService): ScanIsoTankRepository {
    override suspend fun getContainer(
        qrCode: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode)
    }

    override suspend fun saveIsoTank(request: SaveIsoTankRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveIsoTank(request)
    }
}