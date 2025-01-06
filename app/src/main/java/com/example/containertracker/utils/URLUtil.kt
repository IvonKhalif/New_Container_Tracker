package com.example.containertracker.utils

object URLUtil {
    fun createURLReport(
        userId: String,
        startDate: String,
        endDate: String,
        qrCode: String,
        containerCode: String
    ): String {
        return "${NetworkUtil.BASE_URL}tracking/container/history/report?start_date=$startDate&end_date=$endDate&status=&qr_code=$qrCode&account=$userId&id_location=&code_container=$containerCode"
    }
}