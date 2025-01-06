package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.domain.localsales.LocalSalesRepository

class SaveMarkingLocalSalesUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        request: SaveMarkingRequest
    ) = repository.saveMarking(request)
}