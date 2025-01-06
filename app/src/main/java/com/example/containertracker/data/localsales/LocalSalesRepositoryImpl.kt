package com.example.containertracker.data.localsales

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.domain.localsales.LocalSalesRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class LocalSalesRepositoryImpl(private val service: LocalSalesService) : LocalSalesRepository {
    override suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode, containerCode, flag)
    }

    override suspend fun saveHistory(request: SaveLocalSalesRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveLocalSales(request)
    }

    override suspend fun getHistory(
        request: HistoryRequest
    ): NetworkResponse<RetrofitResponse<List<HistoryModel>>, GenericErrorResponse> {
        return service.getHistory(
            qrCode = request.qrCode,
            locationId = request.locationId,
            userId = request.userId,
            status = request.status,
            startDate = request.startDate,
            endDate = request.endDate,
            containerCode = request.containerCode
        )
    }

    override suspend fun scanFlexi(qrCode: String, containerCode: String?, flag: String): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.scanFlexiLocalSales(qrCode, containerCode, flag)
    }

    override suspend fun saveFlexi(request: SaveFlexiRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveFlexiLocalSales(request)
    }

    override suspend fun scanMarking(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.scanMarkingLocalSales(qrCode, containerCode, flag)
    }

    override suspend fun saveMarking(request: SaveMarkingRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveMarkingLocalSales(request)
    }

    override suspend fun scanSeal(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.scanSealLocalSales(qrCode, containerCode, flag)
    }

    override suspend fun saveSeal(request: SaveSealRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveSealLocalSales(request)
    }
}