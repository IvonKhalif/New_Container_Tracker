package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.domain.localsales.LocalSalesRepository

class SaveFlexiLocalSalesUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        request: SaveFlexiRequest
    ) = repository.saveFlexi(request)
}