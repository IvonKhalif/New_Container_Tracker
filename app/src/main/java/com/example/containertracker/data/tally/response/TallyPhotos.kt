package com.example.containertracker.data.tally.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TallyPhotos(
    @SerializedName("id_tally_sheet_photo") var idTallySheetPhoto: String?,
    @SerializedName("id_tally_sheet") var idTallySheet: String?,
    @SerializedName("photo_1") var photo1: String?,
    @SerializedName("photo_2") var photo2: String?,
    @SerializedName("photo_3") var photo3: String?,
    @SerializedName("photo_4") var photo4: String?,
    @SerializedName("photo_5") var photo5: String?,
    @SerializedName("photo_6") var photo6: String?,
    @SerializedName("photo_7") var photo7: String?,
    @SerializedName("photo_8") var photo8: String?,
    @SerializedName("photo_9") var photo9: String?,
    @SerializedName("photo_10") var photo10: String?,
    @SerializedName("photo_11") var photo11: String?,
    @SerializedName("photo_12") var photo12: String?,
    @SerializedName("photo_13") var photo13: String?,
    @SerializedName("photo_14") var photo14: String?,
    @SerializedName("photo_15") var photo15: String?,
    @SerializedName("photo_16") var photo16: String?,
    @SerializedName("photo_17") var photo17: String?,
    @SerializedName("photo_18") var photo18: String?,
    @SerializedName("photo_19") var photo19: String?,
    @SerializedName("photo_20") var photo20: String?,
    @SerializedName("photo_21") var photo21: String?,
    @SerializedName("photo_22") var photo22: String?,
    @SerializedName("photo_23") var photo23: String?,
    @SerializedName("photo_24") var photo24: String?,
    @SerializedName("photo_25") var photo25: String?,
    @SerializedName("photo_26") var photo26: String?,
    @SerializedName("photo_27") var photo27: String?,
    @SerializedName("photo_28") var photo28: String?,
    @SerializedName("photo_29") var photo29: String?,
    @SerializedName("photo_30") var photo30: String?,
): Parcelable
