package com.example.containertracker.domain.marking.usecase

import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.data.marking.requests.SaveMarkingRequest
import com.example.containertracker.domain.flexi.ScanFlexiRepository
import com.example.containertracker.domain.marking.ScanMarkingRepository

class SaveMarkingUseCase(private val repository: ScanMarkingRepository) {
    suspend operator fun invoke(
        request: SaveMarkingRequest
    ) = repository.saveMarking(request)
}