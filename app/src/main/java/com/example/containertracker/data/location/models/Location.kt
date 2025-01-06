package com.example.containertracker.data.location.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    @SerializedName("id_location") var id: String,
    @SerializedName("location_name") var name: String,
    @SerializedName("location_type") var type: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("detail") var detail: String? = null
): Parcelable