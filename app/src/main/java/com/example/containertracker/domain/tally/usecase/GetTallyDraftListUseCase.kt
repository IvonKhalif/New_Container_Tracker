package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class GetTallyDraftListUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        accountId: String
    ) = repository.getDraftTallyList(accountId)
}