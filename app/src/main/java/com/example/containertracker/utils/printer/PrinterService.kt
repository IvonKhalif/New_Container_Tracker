package com.example.containertracker.utils.printer

import com.printer.io.BluetoothPort
import com.printer.io.PortManager

/**
 * Created by yovi.putra on 18/07/22"
 * Project name: Container Tracker
 **/


class PrinterService {
    private var portManager: PortManager? = null

    // buffering
    private val buffer = ByteArray(100)

    fun connect(macAddress: String) {
        portManager = BluetoothPort(macAddress)
        val isOpened = portManager?.openPort()

        val len = portManager?.readData(buffer) ?: 0
        if (len > 0) {

        }
    }
}