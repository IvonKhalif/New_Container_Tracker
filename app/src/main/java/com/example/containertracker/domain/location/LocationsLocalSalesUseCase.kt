package com.example.containertracker.domain.location

class LocationsLocalSalesUseCase(private val repository: LocationRepository) {
    suspend operator fun invoke(accountId: String) = repository.getLocationsLocalSales(accountId)
}