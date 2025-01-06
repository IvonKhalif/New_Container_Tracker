package com.example.containertracker.data.container.requests

import com.google.gson.annotations.SerializedName

data class SaveContainerHistoryRequest(
    @SerializedName("qr_code") val qrCode: String? = null,
    @SerializedName("id_location") val locationId: String? = null,
    @SerializedName("account") val userId: String? = null,
    @SerializedName("seal_id") val sealId: String? = null,
    @SerializedName("voyage_id_out") val voyageIdOut: String? = null,
    @SerializedName("voyage_id_in") val voyageIdIn: String? = null,
    @SerializedName("so_number") val soNumber: String? = null,
    @SerializedName("tarra_weight") val tarraWeight: String? = null,
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("rightside_condition") val rightSideCondition: String? = null,
    @SerializedName("rightside_condition_image") val rightSideConditionImage: String? = null,
    @SerializedName("leftside_condition") val leftSideCondition: String? = null,
    @SerializedName("leftside_condition_image") val leftSideConditionImage: String? = null,
    @SerializedName("roofside_condition") val roofSideCondition: String? = null,
    @SerializedName("roofside_condition_image") val roofSideConditionImage: String? = null,
    @SerializedName("floorside_condition") var floorSideCondition: String? = null,
    @SerializedName("floorside_condition_image") val floorSideConditionImage: String? = null,
    @SerializedName("frontdoor_condition") val frontDoorCondition: String? = null,
    @SerializedName("frontdoor_condition_image") val frontDoorConditionImage: String? = null,
    @SerializedName("backdoor_condition") val backDoorCondition: String? = null,
    @SerializedName("backdoor_condition_image") val backDoorConditionImage: String? = null,
    @SerializedName("defect_photo_1") val defectPhoto1: String? = null,
    @SerializedName("defect_photo_2") val defectPhoto2: String? = null,
    @SerializedName("defect_photo_3") val defectPhoto3: String? = null,
    @SerializedName("defect_photo_4") val defectPhoto4: String? = null,
    @SerializedName("status") val rejectionStatus: String? = null,
    @SerializedName("manufacture_date") val manufactureDate: String? = null,
    @SerializedName("type_scan") val typeScan: String? = null
)