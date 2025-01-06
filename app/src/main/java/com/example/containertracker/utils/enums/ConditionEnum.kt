package com.example.containertracker.utils.enums

import android.os.Parcelable
import com.example.containertracker.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ConditionEnum(val type: String) : Parcelable {
    GOOD("Good"),
    HOLE("Hole"),
    DENT("Dent"),
    BULGING("Bulging"),
    SCRATCH("Scratch")
}

fun ConditionEnum?.isGood() = this == ConditionEnum.GOOD

fun String?.getContainerCondition(): ConditionEnum {
    return ConditionEnum.values().find { it.type == this } ?: ConditionEnum.GOOD
}

fun ConditionEnum.getIcon() = if (this == ConditionEnum.GOOD) {
    R.drawable.ic_check_18dp
} else {
    R.drawable.ic_warning_18dp
}

fun ConditionEnum?.orGood() = this ?: ConditionEnum.GOOD