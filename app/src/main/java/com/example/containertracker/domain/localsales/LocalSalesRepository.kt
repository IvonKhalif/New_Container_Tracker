package com.example.containertracker.domain.localsales

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface LocalSalesRepository {
    suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveHistory(request: SaveLocalSalesRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun getHistory(request: HistoryRequest): NetworkResponse<RetrofitResponse<List<HistoryModel>>, GenericErrorResponse>

    suspend fun scanFlexi(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveFlexi(request: SaveFlexiRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun scanMarking(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveMarking(request: SaveMarkingRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun scanSeal(
        qrCode: String,
        containerCode: String?,
        flag: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveSeal(request: SaveSealRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}