package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ContainerSidesEnum(val type: String) : Parcelable {
    ROOF("Roof Condition"),
    FLOOR("Floor Condition"),
    BACK("Back Condition"),
    DOOR("Door Condition"),
    LEFT("Left Condition"),
    RIGHT("Right Condition"),
    WALL("Wall Condition")
}