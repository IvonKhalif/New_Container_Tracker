package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ContainerTallyConditionEnum(val desc: String) : Parcelable {
    SELECT_CONDITION("Select Condition"),
    GOOD("Good"),
    CLEAN("Clean")
}