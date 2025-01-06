package com.example.containertracker.data.container.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContainerDetail(
    @SerializedName("id_container") var id: String,
    @SerializedName("code_container") var codeContainer: String,
    @SerializedName("unique_id_container") var uniqueId: String,
    @SerializedName("color_container") var color: String?,
    @SerializedName("long_container") var long: String?,
    @SerializedName("wide_container") var wide: String?,
    @SerializedName("tall_container") var tall: String?,
    @SerializedName("rightside_condition") var rightSideCondition: String?,
    @SerializedName("leftside_condition") var leftSideCondition: String?,
    @SerializedName("roofside_condition") var roofSideCondition: String?,
    @SerializedName("floorside_condition") var floorSideCondition: String?,
    @SerializedName("frontdoor_condition") var frontDoorSideCondition: String?,
    @SerializedName("backdoor_condition") var backDoorSideCondition: String?,
    @SerializedName("img_base64") var imgBase64: String?
): Parcelable