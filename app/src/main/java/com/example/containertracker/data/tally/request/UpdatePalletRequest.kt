package com.example.containertracker.data.tally.request

import com.google.gson.annotations.SerializedName

data class UpdatePalletRequest(
    @SerializedName("id_account") var accountId: String?,
    @SerializedName("id_tally_sheet") var idTallySheet: String?,
    @SerializedName("id_sales_order_detail") var salesOrderDetailId: String?,
    @SerializedName("id_tally_sheet_pallet") var idTallySheetPallet: String?,
    @SerializedName("pallet_id") var palletId: String?,
    @SerializedName("batch_number") var batchNumber: String?,
    @SerializedName("qty") var quantity: Int?,
    @SerializedName("reject") var reject: String?,
    @SerializedName("loaded") var loaded: Int?,
    @SerializedName("actual_batch") var actualBatch: String?,
    @SerializedName("remarks") var remarks: String?,
)
