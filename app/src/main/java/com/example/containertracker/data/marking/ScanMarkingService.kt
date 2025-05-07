package com.example.containertracker.data.marking

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
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

interface ScanMarkingService {
    @GET("v1/sales-order/scan-marking/")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v1/sales-order/scan-marking/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveMarking(
        @Body request: SaveMarkingRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}