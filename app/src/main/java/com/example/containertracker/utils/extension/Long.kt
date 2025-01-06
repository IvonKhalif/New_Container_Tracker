package com.example.containertracker.utils.extension

fun Long?.orZero() = this ?: 0L
fun Long?.isNullOrZero() = this.orZero() == 0L
fun Long?.isNotNullOrZero() = !this.isNullOrZero()