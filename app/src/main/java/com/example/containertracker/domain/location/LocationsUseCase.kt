package com.example.containertracker.domain.location

class LocationsUseCase(private val repository: LocationRepository) {
    suspend operator fun invoke(
        accountId: String,
        isContainerLaden: Boolean = false
    ) = when (isContainerLaden){
        false -> repository.getLocations(accountId)
        true -> repository.getLocationsLaden(accountId)
    }
}