package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository

class ScanSPMUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        barcode: String,
        accountId: String?,
        tallySheetId: String,
        flag: String
    ) = repository.scanSPM(barcode, accountId, tallySheetId, flag)
}