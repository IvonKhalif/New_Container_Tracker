package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.data.tally.request.SubmitStatusTallyRequest
import com.example.containertracker.domain.tally.TallyManagementRepository

class SubmitStatusTallyUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        idTallySheet: String,
        accountId: String?
    ) = repository.submitStatusTally(SubmitStatusTallyRequest(idTallySheet, accountId))
}