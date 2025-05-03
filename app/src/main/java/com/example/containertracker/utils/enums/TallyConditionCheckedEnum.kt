package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TallyConditionCheckedEnum(val desc: String) : Parcelable {
    YES("yes"),
    NO("no"),
    Y("Y"),
    N("N"),
}