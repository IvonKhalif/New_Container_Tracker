package com.example.containertracker.data.location

import com.example.containertracker.data.location.models.Location
import com.example.containertracker.domain.location.LocationRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse

class LocationRepositoryImpl(
    private val service: LocationService
): LocationRepository {
    override suspend fun getLocations(accountId: String): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse> =
        service.getLocations(accountId)

    override suspend fun getLocationsLocalSales(accountId: String): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse> =
        service.getLocationsLocalSales(accountId)

    override suspend fun getLocationsLaden(accountId: String): NetworkResponse<RetrofitResponse<List<Location>>, GenericErrorResponse> =
        service.getLocationsLaden(accountId)
}