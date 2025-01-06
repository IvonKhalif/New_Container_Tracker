package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.domain.localsales.LocalSalesRepository

class ScanMarkingLocalSalesUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        qrCode: String,
        containerCode: String?,
        flag: String
    ) = repository.scanMarking(qrCode, containerCode, flag)
}