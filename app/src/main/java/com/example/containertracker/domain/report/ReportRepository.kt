package com.example.containertracker.domain.report

import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitURLResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface ReportRepository {
    suspend fun getReportData(request: HistoryRequest):
            NetworkResponse<RetrofitURLResponse, GenericErrorResponse>
}