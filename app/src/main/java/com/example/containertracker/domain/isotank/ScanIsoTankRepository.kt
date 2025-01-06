package com.example.containertracker.domain.isotank

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.isotank.requests.SaveIsoTankRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ScanIsoTankRepository {
    suspend fun getContainer(
        qrCode: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveIsoTank(request: SaveIsoTankRequest):
            NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}