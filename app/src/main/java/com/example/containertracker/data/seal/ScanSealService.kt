package com.example.containertracker.data.seal

import com.example.containertracker.data.container.models.Container
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

interface ScanSealService {
    @GET("v1/sales-order/scan-seal/")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v1/sales-order/scan-seal/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveSeal(
        @Body request: SaveSealRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}