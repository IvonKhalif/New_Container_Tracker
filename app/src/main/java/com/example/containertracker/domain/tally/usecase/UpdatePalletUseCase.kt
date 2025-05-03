package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.data.tally.request.SavePalletRequest
import com.example.containertracker.data.tally.request.UpdatePalletRequest
import com.example.containertracker.domain.tally.TallyManagementRepository

class UpdatePalletUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        request: UpdatePalletRequest
    ) = repository.updatePallet(request)
}