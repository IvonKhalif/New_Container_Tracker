package com.example.containertracker.data.history.requests

import com.google.gson.annotations.SerializedName

data class HistoryRequest(
    @SerializedName("qr_code") val qrCode: String? = null,
    @SerializedName("id_location") val locationId: String? = null,
    @SerializedName("account") val userId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("batch_id") val batchId: String? = null,
    @SerializedName("code_container") val containerCode: String? = null,
)