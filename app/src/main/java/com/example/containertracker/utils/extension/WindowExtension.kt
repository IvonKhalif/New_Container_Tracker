package com.example.containertracker.utils.extension

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.setFullScreen() {
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView) ?: return

    WindowCompat.setDecorFitsSystemWindows(window, false)

    with (windowInsetsController) {
        // Configure the behavior of the hidden system bars
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
        // Hide both the status bar and the navigation bar
        hide(WindowInsetsCompat.Type.systemBars())
   }
}