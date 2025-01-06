package com.example.containertracker.domain.report

import com.example.containertracker.data.history.requests.HistoryRequest

class GetReportDataUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(
        request: HistoryRequest
    ) = repository.getReportData(request)
}