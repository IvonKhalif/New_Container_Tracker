package com.example.containertracker.data.localsales

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface LocalSalesService {
    @GET("v2/local-sales/tracking/container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("status") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/local-sales/tracking/container/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveLocalSales(
        @Body request: SaveLocalSalesRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/local-sales/tracking/container/history")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getHistory(
        @Query("qr_code") qrCode: String?,
        @Query("id_location") locationId: String?,
        @Query("account") userId: String?,
        @Query("status") status: String?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("code_container") containerCode: String?
    ): NetworkResponse<RetrofitResponse<List<HistoryModel>>, GenericErrorResponse>

    @GET("v2/local-sales/sales-order/scan-flexi/")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun scanFlexiLocalSales(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("status") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/local-sales/sales-order/scan-flexi/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveFlexiLocalSales(
        @Body request: SaveFlexiRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/local-sales/sales-order/scan-marking/")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun scanMarkingLocalSales(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("status") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/local-sales/sales-order/scan-marking/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveMarkingLocalSales(
        @Body request: SaveMarkingRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/local-sales/sales-order/scan-seal/")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun scanSealLocalSales(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("status") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/local-sales/sales-order/scan-seal/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveSealLocalSales(
        @Body request: SaveSealRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

}