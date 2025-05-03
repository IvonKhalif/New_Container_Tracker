package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class ScanPalletUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        barcode: String,
        accountId: String?,
        tallySheetId: String
    ) = repository.scanPallet(barcode, accountId, tallySheetId)
}