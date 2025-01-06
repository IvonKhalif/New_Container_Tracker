package com.example.containertracker.data.repair

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.repair.request.SaveContainerRepairRequest
import com.example.containertracker.domain.repair.ContainerRepairRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ContainerRepairRepositoryImpl(private val service: ContainerRepairService): ContainerRepairRepository {
    override suspend fun getContainer(
        qrCode: String?,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode, containerCode, flag)
    }

    override suspend fun saveRepair(request: SaveContainerRepairRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveContainerRepair(request)
    }
}