package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FlagScanEnum(val type: String) : Parcelable {
    SCAN("SCAN"),
    INPUT("INPUT")
}