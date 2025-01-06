package com.example.containertracker.domain.container

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ContainerRepository {
    suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveHistory(request: SaveContainerHistoryRequest):
            NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun getList(
        keyword: String?
    ): NetworkResponse<RetrofitResponse<List<ContainerDetail>>, GenericErrorResponse>
    suspend fun getQRContainer(id: String): NetworkResponse<RetrofitResponse<ContainerDetail>, GenericErrorResponse>
}