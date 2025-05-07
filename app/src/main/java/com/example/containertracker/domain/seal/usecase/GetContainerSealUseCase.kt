package com.example.containertracker.domain.seal.usecase

import com.example.containertracker.domain.seal.ScanSealRepository

class GetContainerSealUseCase(private val repository: ScanSealRepository) {
    suspend operator fun invoke(
        qrCode: String
    ) = repository.getContainer(qrCode)
}