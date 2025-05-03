package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class GetTallyDetailUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        idTally: String
    ) = repository.getTallySheetDetail(idTally)
}