package com.example.containertracker.utils.printer

/**
 * Created by yovi.putra on 18/07/22"
 * Project name: Container Tracker
 **/


data class PrinterUiModel(
    val address: String,
    val name: String,
    val bonded: Boolean = false
) {

    val printerName
        get() = name.ifEmpty { address }
}