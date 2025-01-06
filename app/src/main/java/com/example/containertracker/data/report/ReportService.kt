package com.example.containertracker.data.report

import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitURLResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ReportService {
    @GET("v1/tracking/container/history")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getReportData(
        @Query("qr_code") qrCode: String?,
        @Query("id_location") locationId: String?,
        @Query("account") userId: String?,
        @Query("status") status: String?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): NetworkResponse<RetrofitURLResponse, GenericErrorResponse>
}