package com.example.containertracker.ui.media.image_preview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/

@Parcelize
data class ImagePreviewConfig(
    val buttonRemoveEnable: Boolean = true,
    val buttonEditEnable: Boolean = true,
    val buttonDoneEnable: Boolean = true,
    val buttonBackEnable: Boolean = true
) : Parcelable
