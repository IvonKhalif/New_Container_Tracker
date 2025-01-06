package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.domain.localsales.LocalSalesRepository

class SaveSealLocalSalesUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        request: SaveSealRequest
    ) = repository.saveSeal(request)
}