package com.example.containertracker.domain.containerladen

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.containerladen.models.ContainerLadenDetail
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ContainerLadenRepository {
    suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveHistory(request: SaveContainerHistoryRequest):
            NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun getHistory(id: String): NetworkResponse<RetrofitResponse<ContainerLadenDetail>, GenericErrorResponse>
}