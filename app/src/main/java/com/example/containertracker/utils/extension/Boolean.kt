package com.example.containertracker.utils.extension

fun Boolean?.orTrue() = this ?: true
fun Boolean?.orFalse() = this ?: false