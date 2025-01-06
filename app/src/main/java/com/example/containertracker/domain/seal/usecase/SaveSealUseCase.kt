package com.example.containertracker.domain.seal.usecase

import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.domain.seal.ScanSealRepository

class SaveSealUseCase(private val repository: ScanSealRepository) {
    suspend operator fun invoke(
        request: SaveSealRequest
    ) = repository.saveSeal(request)
}