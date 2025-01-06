package com.example.containertracker.data.location

import com.example.containertracker.data.location.models.Location
import com.example.containertracker.data.user.models.User
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LocationService {
    @GET("v1/location")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getLocations(
        @Query("account") accountId: String
    ): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse>

    @GET("v1/location/local-sales")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getLocationsLocalSales(
        @Query("account") accountId: String
    ): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse>

    @GET("v1/location/laden")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getLocationsLaden(
        @Query("account") accountId: String
    ): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse>
}