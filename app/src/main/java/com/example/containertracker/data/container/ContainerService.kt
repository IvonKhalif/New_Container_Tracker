package com.example.containertracker.data.container

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.*

interface ContainerService {
    @GET("v1/tracking/container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("flag") flagScan: String
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @POST("v2/tracking/container/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveContainerHistory(
        @Body request: SaveContainerHistoryRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v1/container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainerList(
        @Query("keyword") keyword: String?,
    ): NetworkResponse<RetrofitResponse<List<ContainerDetail>>, GenericErrorResponse>

    @GET("v1/container/detail")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainerQR(
        @Query("id_container") id: String
    ): NetworkResponse<RetrofitResponse<ContainerDetail>, GenericErrorResponse>
}