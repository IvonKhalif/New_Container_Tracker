package com.example.containertracker.domain.flexi.usecase

import com.example.containertracker.domain.flexi.ScanFlexiRepository

class GetContainerFlexiUseCase(private val repository: ScanFlexiRepository) {
    suspend operator fun invoke(
        qrCode: String,
        containerCode: String?,
        flag: String
    ) = repository.getContainer(qrCode, containerCode, flag)
}