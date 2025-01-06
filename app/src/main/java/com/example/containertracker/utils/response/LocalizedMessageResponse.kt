package com.example.containertracker.utils.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class LocalizedMessageResponse(
    @SerializedName("en") val en: String,
    @SerializedName("id") val id: String
) : Parcelable