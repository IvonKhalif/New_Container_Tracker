package com.example.containertracker.data.containerladen

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.containerladen.models.ContainerLadenDetail
import com.example.containertracker.data.history.model.HistoryModel
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

interface ContainerLadenService {
    @GET("v2/laden/tracking/container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("flag") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/laden/tracking/container/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveContainerHistory(
        @Body request: SaveContainerHistoryRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/laden/tracking/container/detail")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainerDetail(
        @Query("id_tracking_container_laden") id: String
    ): NetworkResponse<RetrofitResponse<ContainerLadenDetail>, GenericErrorResponse>
}