package com.example.containertracker.widget.select_image

import com.example.containertracker.R

/**
 * Created by yovi.putra on 20/07/22"
 * Project name: Container Tracker
 **/

sealed class SelectImageMenuType(
    val icon: Int, val title: Int
) {
    object Gallery : SelectImageMenuType(
        icon = R.drawable.ic_baseline_photo_library_24,
        title = R.string.gallery
    )

    object Camera : SelectImageMenuType(
        icon = R.drawable.ic_outline_camera_alt_24,
        title = R.string.camera
    )
}
