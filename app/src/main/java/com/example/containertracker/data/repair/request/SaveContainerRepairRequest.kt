package com.example.containertracker.data.repair.request

import com.google.gson.annotations.SerializedName

data class SaveContainerRepairRequest(
    @SerializedName("qr_code") val qrCode: String? = null,
    @SerializedName("code_container") val containerCode: String? = null,
    @SerializedName("flag") var flagScan: String? = null,
    @SerializedName("repair_photo_1") val photo1: String? = null,
    @SerializedName("repair_photo_2") val photo2: String? = null,
    @SerializedName("repair_photo_3") val photo3: String? = null,
    @SerializedName("repair_photo_4") val photo4: String? = null,
    @SerializedName("repair_photo_5") val photo5: String? = null,
    @SerializedName("repair_photo_6") val photo6: String? = null,
    @SerializedName("repair_photo_7") val photo7: String? = null,
    @SerializedName("repair_photo_8") val photo8: String? = null
)
