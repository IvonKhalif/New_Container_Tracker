package com.example.containertracker.domain.repair

class ScanContainerRepairUseCase(private val repository: ContainerRepairRepository) {
    suspend operator fun invoke(
        qrCode: String?,
        containerCode: String?,
        flag: String,
    ) = repository.getContainer(qrCode, containerCode, flag)
}