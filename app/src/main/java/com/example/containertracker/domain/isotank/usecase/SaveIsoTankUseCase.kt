package com.example.containertracker.domain.isotank.usecase

import com.example.containertracker.data.isotank.requests.SaveIsoTankRequest
import com.example.containertracker.domain.isotank.ScanIsoTankRepository

class SaveIsoTankUseCase(private val repository: ScanIsoTankRepository) {
    suspend operator fun invoke(
        request: SaveIsoTankRequest
    ) = repository.saveIsoTank(request)
}