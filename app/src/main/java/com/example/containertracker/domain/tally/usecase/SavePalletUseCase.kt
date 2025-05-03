package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.data.tally.request.SavePalletRequest
import com.example.containertracker.domain.tally.TallyManagementRepository

class SavePalletUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        request: SavePalletRequest
    ) = repository.savePallet(request)
}