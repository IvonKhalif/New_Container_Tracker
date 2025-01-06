package com.example.containertracker.data.containerladen.models

import android.os.Parcelable
import com.example.containertracker.data.history.model.HistoryModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContainerLadenDetail(
    @SerializedName("id_tracking_container") var id: String,
    @SerializedName("code_container") var codeContainer: String,
    @SerializedName("unique_id_container") var uniqueId: String,
    @SerializedName("status") var status: String?,
    @SerializedName("rightside_condition") var rightSideCondition: String?,
    @SerializedName("leftside_condition") var leftSideCondition: String?,
    @SerializedName("roofside_condition") var roofSideCondition: String?,
    @SerializedName("floorside_condition") var floorSideCondition: String?,
    @SerializedName("frontdoor_condition") var frontDoorSideCondition: String?,
    @SerializedName("backdoor_condition") var backDoorSideCondition: String?,
    @SerializedName("detail") var detailHistoryList: List<HistoryModel>?
): Parcelable
