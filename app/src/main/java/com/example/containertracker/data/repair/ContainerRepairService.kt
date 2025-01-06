package com.example.containertracker.data.repair

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.repair.request.SaveContainerRepairRequest
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

interface ContainerRepairService {
    @GET("v2/inventory-container/scan-container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String?,
        @Query("code_container") containerCode: String?,
        @Query("flag") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/inventory-container/save-repair-container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveContainerRepair(
        @Body request: SaveContainerRepairRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}