package com.example.containertracker.ui.flexi.form

import android.os.Parcelable
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlexiFormParam(
    val flexiCap: String,
    val flexiNumber: String,
    val containerData: Container,
    val containerPictures: List<GenericSelectImageUiModel>,
): Parcelable