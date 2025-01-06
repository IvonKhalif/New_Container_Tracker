package com.example.containertracker.utils.extension

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val arrayList = arrayListOf<T>()
    arrayList.addAll(this)
    return arrayList
}