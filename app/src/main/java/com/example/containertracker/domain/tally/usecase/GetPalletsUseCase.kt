package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class GetPalletsUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        idTallySheet: String
    ) = repository.getPallets(idTallySheet)
}