package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.domain.localsales.LocalSalesRepository

class ScanLocalSalesContainerUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        qrCode: String,
        containerCode: String?,
        flag: String
    ) = repository.getContainer(qrCode, containerCode, flag)
}