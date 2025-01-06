package com.example.containertracker.ui.home.containerdefect

import android.os.Parcelable
import android.util.Base64
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.utils.enums.RemarkStatusEnum
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ContainerDefectParam(
    val containerRemark: String?,
    val containerPictures: List<GenericSelectImageUiModel>,
): Parcelable {
    fun getBase64(position: Int): String? {
        val filePath = containerPictures.firstOrNull { it.position == position.toString() }?.imageFilePath

        if (filePath.isNullOrBlank()) return null

        val file = File(filePath)

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }
}
