package com.example.containertracker.data.user.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id_account") var id: String,
    @SerializedName("name_account") var name: String,
    @SerializedName("id_level") var levelId: String,
    @SerializedName("level") var level: String,
    @SerializedName("id_department") var departmentId: String,
    @SerializedName("name_department") var department: String,
    @SerializedName("id_location") var locationId: String?,
    @SerializedName("location_name") var location: String?,
    @SerializedName("expired_date") var expiredDate: String?
): Parcelable