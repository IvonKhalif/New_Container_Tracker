package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.domain.localsales.LocalSalesRepository

class SaveLocalSalesContainerUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        request: SaveLocalSalesRequest
    ) = repository.saveHistory(request)
}