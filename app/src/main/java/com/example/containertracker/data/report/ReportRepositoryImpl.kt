package com.example.containertracker.data.report

import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.domain.report.ReportRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitURLResponse
import com.haroldadmin.cnradapter.NetworkResponse

class ReportRepositoryImpl(private val service: ReportService): ReportRepository {
    override suspend fun getReportData(request: HistoryRequest): NetworkResponse<RetrofitURLResponse, GenericErrorResponse> {
        return service.getReportData(
            qrCode = request.qrCode,
            locationId = request.locationId,
            userId = request.userId,
            status = request.status,
            startDate = request.startDate,
            endDate = request.endDate
        )
    }
}