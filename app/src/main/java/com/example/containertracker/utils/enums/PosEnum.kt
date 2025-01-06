package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PosEnum(val posName: String, val posId: Int) : Parcelable {
    POS1("Pos 1", 1),
    POS2("Pos 2", 2),
    POS3("Pos 3", 3),
    POS4("Pos 4", 4),
    POS5("Pos 5", 5),
    POS6("Pos 6", 6),
    POS7("Pos 7", 7),
}