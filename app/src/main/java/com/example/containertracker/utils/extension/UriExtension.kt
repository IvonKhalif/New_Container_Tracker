package com.example.containertracker.utils.extension

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.containertracker.BuildConfig
import java.io.File

fun File.toUriProvider(context: Context): Uri {
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        this
    )
}