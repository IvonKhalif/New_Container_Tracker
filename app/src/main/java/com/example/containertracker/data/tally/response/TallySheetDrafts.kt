package com.example.containertracker.data.tally.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TallySheetDrafts(
    @SerializedName("id_tracking_container") var id: String?,
    @SerializedName("id_tally_sheet") var idTallySheet: String?,
    @SerializedName("id_sales_order_detail") var idSalesOrderDetail: String?,
    @SerializedName("id_tracking_history_container") var historyId: Int?,
    @SerializedName("code_container") var codeContainer: String?,
): Parcelable
