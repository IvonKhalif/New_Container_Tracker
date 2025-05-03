package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class GetPalletDetailUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        idTallySheetPallet: String
    ) = repository.getPalletDetail(idTallySheetPallet)
}