package com.example.containertracker.data.marking.requests

import com.google.gson.annotations.SerializedName

data class SaveMarkingRequest(
    @SerializedName("account") val userId: String? = null,
    @SerializedName("id_sales_order_detail") var salesOrderDetailId: String? = null,
    @SerializedName("type_container") var typeContainer: String? = null,
    @SerializedName("photo_1") val photo1: String? = null,
    @SerializedName("photo_2") val photo2: String? = null,
    @SerializedName("photo_3") val photo3: String? = null,
    @SerializedName("photo_4") val photo4: String? = null,
    @SerializedName("photo_5") val photo5: String? = null,
    @SerializedName("photo_6") val photo6: String? = null,
    @SerializedName("photo_7") val photo7: String? = null,
    @SerializedName("photo_8") val photo8: String? = null
)
