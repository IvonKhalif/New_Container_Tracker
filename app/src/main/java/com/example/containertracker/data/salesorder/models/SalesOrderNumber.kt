package com.example.containertracker.data.salesorder.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SalesOrderNumber(
    @SerializedName("so_number") var number: String,
): Parcelable