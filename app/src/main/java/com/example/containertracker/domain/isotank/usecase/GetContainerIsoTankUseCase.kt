package com.example.containertracker.domain.isotank.usecase

import com.example.containertracker.domain.isotank.ScanIsoTankRepository

class GetContainerIsoTankUseCase(private val repository: ScanIsoTankRepository) {
    suspend operator fun invoke(
        qrCode: String
    ) = repository.getContainer(qrCode)
}