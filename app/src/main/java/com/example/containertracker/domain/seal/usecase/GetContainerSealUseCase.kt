package com.example.containertracker.domain.seal.usecase

import com.example.containertracker.domain.seal.ScanSealRepository

class GetContainerSealUseCase(private val repository: ScanSealRepository) {
    suspend operator fun invoke(
        qrCode: String,
        containerCode: String?,
        flag: String
    ) = repository.getContainer(qrCode, containerCode, flag)
}