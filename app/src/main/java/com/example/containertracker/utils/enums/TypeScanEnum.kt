package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TypeScanEnum(val type: String) : Parcelable {
    OPEN("OPEN"),
    CLOSING("CLOSING")
}