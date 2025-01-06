package com.example.containertracker.ui.history.container_image_detail.models

import com.example.containertracker.utils.NetworkUtil
import com.example.containertracker.utils.enums.ConditionEnum
import com.example.containertracker.utils.enums.ContainerSidesEnum

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/

data class HistoryConditionImageUiModel(
    val imageUrl: String,
    val position: ContainerSidesEnum,
    val condition: ConditionEnum
) {
    val positionName
        get() = position.type.replace("Condition", "").trim()

    val imageFullUrl = "${NetworkUtil.SERVER_HOST}$imageUrl"
}