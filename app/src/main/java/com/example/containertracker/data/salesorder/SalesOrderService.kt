package com.example.containertracker.data.salesorder

import com.example.containertracker.data.location.models.Location
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SalesOrderService {
    @GET("v1/so")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getSONumberList(): NetworkResponse<RetrofitResponse<List<SalesOrderNumber>>, GenericErrorResponse>
}