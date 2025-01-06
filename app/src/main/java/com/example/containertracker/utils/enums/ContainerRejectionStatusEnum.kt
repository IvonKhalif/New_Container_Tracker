package com.example.containertracker.utils.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ContainerRejectionStatusEnum(val status: String) : Parcelable {
    DEFECT("DEFECT"),
    RELEASE("RELEASE")
}