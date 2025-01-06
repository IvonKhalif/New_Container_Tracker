package com.example.containertracker.data.container

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.domain.container.ContainerRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ContainerRepositoryImpl(private val service: ContainerService) : ContainerRepository {
    override suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode, containerCode, flag)
    }

    override suspend fun saveHistory(request: SaveContainerHistoryRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveContainerHistory(request)
    }

    override suspend fun getList(keyword: String?): NetworkResponse<RetrofitResponse<List<ContainerDetail>>, GenericErrorResponse> {
        return service.getContainerList(keyword)
    }

    override suspend fun getQRContainer(id: String): NetworkResponse<RetrofitResponse<ContainerDetail>, GenericErrorResponse> {
        return service.getContainerQR(id)
    }
}