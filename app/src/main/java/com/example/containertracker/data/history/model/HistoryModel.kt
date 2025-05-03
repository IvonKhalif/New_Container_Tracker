package com.example.containertracker.data.history.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryModel(
    @SerializedName("id_tracking_container") var id: String,
    @SerializedName("id_tracking_history_container") var historyId: Int,
    @SerializedName("code_container") var codeContainer: String,
    @SerializedName("origin") var origin: String?,
    @SerializedName("destination") var destination: String?,
    @SerializedName("location_name") var location: String,
    @SerializedName("status") var status: String?,
    @SerializedName("batch_id") var batchId: String?,
    @SerializedName("datetime") var dateTime: String?,
    @SerializedName("name_account") var nameAccount: String?,
    @SerializedName("seal_id") var sealId: String?,
    @SerializedName("rightside_condition") var rightSideCondition: String? = null,
    @SerializedName("leftside_condition") var leftSideCondition: String? = null,
    @SerializedName("roofside_condition") var roofSideCondition: String? = null,
    @SerializedName("floorside_condition") var floorSideCondition: String? = null,
    @SerializedName("frontdoor_condition") var frontDoorSideCondition: String? = null,
    @SerializedName("backdoor_condition") var backDoorSideCondition: String? = null,
    @SerializedName("rightside_condition_image") var rightSideConditionImage: String? = null,
    @SerializedName("leftside_condition_image") var leftSideConditionImage: String? = null,
    @SerializedName("roofside_condition_image") var roofSideConditionImage: String? = null,
    @SerializedName("floorside_condition_image") var floorSideConditionImage: String? = null,
    @SerializedName("frontdoor_condition_image") var frontDoorSideConditionImage: String? = null,
    @SerializedName("backdoor_condition_image") var backDoorSideConditionImage: String? = null,
    @SerializedName("id_location") var idLocation: Int?,
    @SerializedName("status_reverse") var statusReverse: String?,
    @SerializedName("created_date") var createdDate: String?,
    @SerializedName("created_by") var createdBy: String?,
    @SerializedName("updated_date") var updatedDate: String?,
    @SerializedName("updated_by") var updatedBy: Int?,
    @SerializedName("deleted_date") var deletedDate: String?,
    @SerializedName("deleted_by") var deletedBy: Int?,
): Parcelable