package com.example.containertracker.domain.flexi.usecase

import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.domain.flexi.ScanFlexiRepository

class SaveFlexiUseCase(private val repository: ScanFlexiRepository) {
    suspend operator fun invoke(
        request: SaveFlexiRequest
    ) = repository.saveFlexi(request)
}