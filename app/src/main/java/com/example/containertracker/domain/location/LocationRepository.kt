package com.example.containertracker.domain.location

import com.example.containertracker.data.location.models.Location
import com.example.containertracker.data.user.models.User
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface LocationRepository {
    suspend fun getLocations(accountId: String): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse>
    suspend fun getLocationsLocalSales(accountId: String): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse>
    suspend fun getLocationsLaden(accountId: String): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse>
}