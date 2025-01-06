package com.example.containertracker.domain.history

import com.example.containertracker.data.history.requests.HistoryRequest

class GetHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(
        request: HistoryRequest
    ) = repository.getHistory(request)
}