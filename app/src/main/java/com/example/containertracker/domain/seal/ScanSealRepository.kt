package com.example.containertracker.domain.seal

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ScanSealRepository {
    suspend fun getContainer(
        qrCode: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveSeal(request: SaveSealRequest):
            NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}