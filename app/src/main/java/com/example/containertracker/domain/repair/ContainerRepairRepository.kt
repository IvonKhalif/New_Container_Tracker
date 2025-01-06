package com.example.containertracker.domain.repair

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.repair.request.SaveContainerRepairRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ContainerRepairRepository {
    suspend fun getContainer(
        qrCode: String?,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveRepair(request: SaveContainerRepairRequest):
            NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}