package com.example.containertracker.utils.pickimage

import android.os.Build

object CommonVersionCheck {
    fun isAtLeastJ18() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
    fun isAtLeastM23() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    fun isAtLeastO26() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isAtLeastQ29() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}