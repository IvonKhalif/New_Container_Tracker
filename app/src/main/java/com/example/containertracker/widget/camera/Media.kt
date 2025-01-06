package com.example.containertracker.widget.camera

import android.net.Uri

data class Media(
    val uri: Uri,
    val isVideo: Boolean,
    val date: Long,
)
