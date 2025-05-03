package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.domain.tally.TallyManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SavePhotoUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        tallySheetId: String,
        itemId: String?,
        photo: String
    ) = withContext(Dispatchers.IO) {
        repository.savePhoto(tallySheetId, itemId, photo)
    }
}