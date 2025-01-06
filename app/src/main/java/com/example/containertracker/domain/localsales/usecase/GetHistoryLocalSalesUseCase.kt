package com.example.containertracker.domain.localsales.usecase

import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.domain.localsales.LocalSalesRepository

class GetHistoryLocalSalesUseCase(private val repository: LocalSalesRepository) {
    suspend operator fun invoke(
        request: HistoryRequest
    ) = repository.getHistory(request)
}