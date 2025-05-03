package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class GetContainerTallyUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        qrCode: String,
        containerCode: String?,
        flag: String,
        accountId: String?
    ) = repository.getContainer(qrCode, containerCode, flag, accountId)
}