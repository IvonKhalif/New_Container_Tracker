package com.example.containertracker.domain.history.history_detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/

@Parcelize
data class HistoryDetail(
    val backdoorCondition: String,
    val backdoorConditionImage: String,
    val floorCondition: String,
    val floorConditionImage: String,
    val frontCondition: String,
    val frontConditionImage: String,
    val leftCondition: String,
    val leftConditionImage: String,
    val rightCondition: String,
    val rightConditionImage: String,
    val roofCondition: String,
    val roofConditionImage: String,
): Parcelable
