package com.example.containertracker.utils.extension

import android.graphics.*

fun Bitmap.toGrayscale(): Bitmap?{
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val matrix = ColorMatrix().apply {
        setSaturation(0f)
    }
    val filter = ColorMatrixColorFilter(matrix)

    val paint = Paint().apply {
        colorFilter = filter
    }

    Canvas(bitmap).drawBitmap(this, 0f, 0f, paint)
    return bitmap
}