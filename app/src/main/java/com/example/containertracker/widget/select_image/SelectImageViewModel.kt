package com.example.containertracker.widget.select_image

import androidx.lifecycle.ViewModel

/**
 * Created by yovi.putra on 20/07/22"
 * Project name: Container Tracker
 **/


class SelectImageViewModel : ViewModel() {

    val menu = listOf(
        SelectImageMenuType.Camera,
        SelectImageMenuType.Gallery
    )
}