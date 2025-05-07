package com.example.containertracker.domain.marking

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ScanMarkingRepository {
    suspend fun getContainer(
        qrCode: String,
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveMarking(request: SaveMarkingRequest):
            NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}