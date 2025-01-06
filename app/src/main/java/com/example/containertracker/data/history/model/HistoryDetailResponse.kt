package com.example.containertracker.data.history.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HistoryDetailResponse(
    @SerializedName("backdoor_condition")
    @Expose
    val backdoorCondition: String?,
    @SerializedName("backdoor_condition_image")
    @Expose
    val backdoorConditionImage: String?,
    @SerializedName("code_container")
    @Expose
    val codeContainer: String?,
    @SerializedName("created_by")
    @Expose
    val createdBy: String?,
    @SerializedName("created_date")
    @Expose
    val createdDate: String?,
    @SerializedName("datetime")
    @Expose
    val datetime: String?,
    @SerializedName("deletedBy")
    @Expose
    val deletedBy: String?,
    @SerializedName("deleted_date")
    @Expose
    val deletedDate: String?,
    @SerializedName("floorside_condition")
    @Expose
    val floorCondition: String?,
    @SerializedName("floorside_condition_image")
    @Expose
    val floorConditionImage: String?,
    @SerializedName("frontdoor_condition")
    @Expose
    val frontCondition: String?,
    @SerializedName("frontdoor_condition_image")
    @Expose
    val frontConditionImage: String?,
    @SerializedName("id_location")
    @Expose
    val idLocation: Int?,
    @SerializedName("id_tracking_container")
    @Expose
    val idTrackingContainer: Int?,
    @SerializedName("id_tracking_history_container")
    @Expose
    val idTrackingHistoryContainer: Int?,
    @SerializedName("is_deleted")
    @Expose
    val isDeleted: String?,
    @SerializedName("latitude")
    @Expose
    val latitude: String?,
    @SerializedName("leftside_condition")
    @Expose
    val leftCondition: String?,
    @SerializedName("leftside_condition_image")
    @Expose
    val leftConditionImage: String?,
    @SerializedName("longitude")
    @Expose
    val longitude: String?,
    @SerializedName("rightside_condition")
    @Expose
    val rightCondition: String?,
    @SerializedName("rightside_condition_image")
    @Expose
    val rightConditionImage: String?,
    @SerializedName("roofside_condition")
    @Expose
    val roofCondition: String?,
    @SerializedName("roofside_condition_image")
    @Expose
    val roofConditionImage: String?,
    @SerializedName("seal_id")
    @Expose
    val sealId: String?,
    @SerializedName("status")
    @Expose
    val status: String?,
    @SerializedName("updated_by")
    @Expose
    val updatedBy: String?,
    @SerializedName("updated_date")
    @Expose
    val updatedDate: String?
)