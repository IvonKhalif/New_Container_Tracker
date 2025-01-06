package com.example.containertracker.ui.history.container_image_detail.models

import com.example.containertracker.domain.history.history_detail.model.HistoryDetail
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.utils.enums.getContainerCondition

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/

data class HistoryContainerDetailUiModel(
    val images: List<HistoryConditionImageUiModel>
)

fun HistoryDetail.asUiModel() : HistoryContainerDetailUiModel {
    val imageList = arrayListOf<HistoryConditionImageUiModel>()

    if (floorConditionImage.isNotBlank()) {
        imageList.add(
            HistoryConditionImageUiModel(
                imageUrl = floorConditionImage,
                position = ContainerSidesEnum.FLOOR,
                condition = floorCondition.getContainerCondition()
            )
        )
    }

    if (backdoorConditionImage.isNotBlank()) {
        imageList.add(
            HistoryConditionImageUiModel(
                imageUrl = backdoorConditionImage,
                position = ContainerSidesEnum.BACK,
                condition = backdoorCondition.getContainerCondition()
            )
        )
    }

    if (leftConditionImage.isNotBlank()) {
        imageList.add(
            HistoryConditionImageUiModel(
                imageUrl = leftConditionImage,
                position = ContainerSidesEnum.LEFT,
                condition = leftCondition.getContainerCondition()
            )
        )
    }

    if (frontConditionImage.isNotBlank()) {
        imageList.add(
            HistoryConditionImageUiModel(
                imageUrl = frontConditionImage,
                position = ContainerSidesEnum.DOOR,
                condition = frontCondition.getContainerCondition()
            )
        )
    }

    if (rightConditionImage.isNotBlank()) {
        imageList.add(
            HistoryConditionImageUiModel(
                imageUrl = rightConditionImage,
                position = ContainerSidesEnum.RIGHT,
                condition = rightCondition.getContainerCondition()
            )
        )
    }

    if (roofConditionImage.isNotBlank()) {
        imageList.add(
            HistoryConditionImageUiModel(
                imageUrl = roofConditionImage,
                position = ContainerSidesEnum.ROOF,
                condition = roofCondition.getContainerCondition()
            )
        )
    }

    return HistoryContainerDetailUiModel(
        images = imageList
    )
}