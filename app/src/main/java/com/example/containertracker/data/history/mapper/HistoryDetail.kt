package com.example.containertracker.data.history.mapper

import com.example.containertracker.data.history.model.HistoryDetailResponse
import com.example.containertracker.domain.history.history_detail.model.HistoryDetail

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/
 
fun HistoryDetailResponse.asDomain() = HistoryDetail(
    backdoorCondition = backdoorCondition.orEmpty(),
    backdoorConditionImage = backdoorConditionImage.orEmpty(),
    floorCondition = floorCondition.orEmpty(),
    floorConditionImage = floorConditionImage.orEmpty(),
    frontCondition = frontCondition.orEmpty(),
    frontConditionImage = frontConditionImage.orEmpty(),
    leftCondition = leftCondition.orEmpty(),
    leftConditionImage = leftConditionImage.orEmpty(),
    rightCondition = rightCondition.orEmpty(),
    rightConditionImage = rightConditionImage.orEmpty(),
    roofCondition = roofCondition.orEmpty(),
    roofConditionImage = roofConditionImage.orEmpty()
)