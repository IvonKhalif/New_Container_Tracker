package com.example.containertracker.data.tally.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PalletResponse(
    @SerializedName("id_tally_sheet") var idTallySheet: String?,
    @SerializedName("id_tally_sheet_pallet") var idTallySheetPallet: String?,
    @SerializedName("id_sales_order_detail") var idSalesOrderDetail: String?,
    @SerializedName("pallet_id") var palletId: String?,
    @SerializedName("barcode") var barcode: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("batch_number") var batchNumber: String?,
    @SerializedName("qty") var quantity: Int?,
    @SerializedName("reject") var reject: String?,
    @SerializedName("loaded") var loaded: Int?,
    @SerializedName("actual_batch") var actualBatch: String?,
    @SerializedName("remarks") var remarks: String?,
    @SerializedName("scan_by") var scanBy: String?,
    @SerializedName("scan_date") var scanDate: String?,
    @SerializedName("created_by") var createdBy: Int?,
    @SerializedName("updated_date") var updatedDate: String?,
    @SerializedName("updated_by") var updatedBy: Int?,
    @SerializedName("deleted_date") var deletedDate: String?,
    @SerializedName("deleted_by") var deletedBy: Int?,
    @SerializedName("is_deleted") var isDeleted: String?,
): Parcelable
