package com.example.containertracker.data.containerladen

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.containerladen.models.ContainerLadenDetail
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.domain.containerladen.ContainerLadenRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ContainerLadenRepositoryImpl(private val service: ContainerLadenService): ContainerLadenRepository {
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

    override suspend fun getHistory(id: String): NetworkResponse<RetrofitResponse<ContainerLadenDetail>, GenericErrorResponse> {
        return service.getContainerDetail(id)
    }
}