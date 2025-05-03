package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.TallyManagementRepository
import com.example.containertracker.utils.PreferenceUtils

class DeleteTallySheetUseCase(private val repository: TallyManagementRepository) {
    val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
    suspend operator fun invoke(
        idTallySheet: String
    ) = repository.deleteTallySheet(accountId = user?.id.orEmpty(), idTally = idTallySheet)
}