package com.example.containertracker.data.container.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContainerHistory(
    @SerializedName("location_name") var locationName: String,
    @SerializedName("datetime") var dateTime: String,
    @SerializedName("name_account") var nameAccount: String,
    @SerializedName("no") var no: String?
): Parcelable