package com.example.containertracker.ui.home.containercondition.models

import android.os.Parcelable
import com.example.containertracker.utils.enums.ConditionEnum
import com.example.containertracker.utils.enums.ContainerSidesEnum
import kotlinx.parcelize.Parcelize

/**
 * Created by yovi.putra on 21/07/22"
 * Project name: Container Tracker
 **/

@Parcelize
data class ContainerImageUiModel(
    val imageFilePath: String? = null,
    val position: ContainerSidesEnum,
    val condition: ConditionEnum?
) : Parcelable {

    val positionName
        get() = position.type.replace("Condition", "").trim()
}
