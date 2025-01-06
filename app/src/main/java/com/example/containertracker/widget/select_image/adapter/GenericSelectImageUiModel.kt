package com.example.containertracker.widget.select_image.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenericSelectImageUiModel(
    val imageFilePath: String? = null,
    val position: String,
    val positionName: String? = null
) : Parcelable