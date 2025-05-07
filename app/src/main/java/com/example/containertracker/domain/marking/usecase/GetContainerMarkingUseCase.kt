package com.example.containertracker.domain.marking.usecase

import com.example.containertracker.domain.marking.ScanMarkingRepository

class GetContainerMarkingUseCase (private val repository: ScanMarkingRepository) {
    suspend operator fun invoke(
        qrCode: String
    ) = repository.getContainer(qrCode)
}