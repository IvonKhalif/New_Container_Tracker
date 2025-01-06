package com.example.containertracker.utils.extension

fun Int?.orZero() = this ?: 0
fun Int?.isNullOrZero() = this.orZero() == 0