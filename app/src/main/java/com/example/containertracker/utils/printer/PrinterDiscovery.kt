package com.example.containertracker.utils.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*

/**
 * Created by yovi.putra on 18/07/22"
 * Project name: Container Tracker
 **/

/**
 * Printer discovery to get all printer bluetooth detected
 */
@SuppressLint("MissingPermission")
class PrinterDiscovery(private val context: ContextWrapper?) {
    // buffer store device
    private var mDevices = arrayListOf<PrinterUiModel>()
    // bluetooth adapter
    private var bluetoothAdapter: BluetoothAdapter? = null
    // discovery finished callback
    var onDiscoveryFinished : () -> Unit = {}
    // emit each new device added
    var onDeviceAvailable: (List<PrinterUiModel>) -> Unit = {}

    private val findBluetoothReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // If it's already paired, skip it, because it's been listed
                if (device != null && device.bondState != BluetoothDevice.BOND_BONDED) {
                    add(device = device)
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                onDiscoveryFinished.invoke()
            }
        }
    }

    fun registerDiscovery() {
        // prepare register broadcast receiver
        // register action found
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context?.registerReceiver(findBluetoothReceiver, filter)
        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context?.registerReceiver(findBluetoothReceiver, filter)
    }

    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
        bluetoothAdapter = null

        context?.unregisterReceiver(findBluetoothReceiver)
    }

    // ini bluetooth adapter
    private fun initBluetoothAdapter() {
        bluetoothAdapter = (context?.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter

        val deviceBonded = bluetoothAdapter?.bondedDevices.orEmpty().map {
            PrinterUiModel(address = it.address, name = it.name, bonded = true)
        }
        mDevices.clear()
        mDevices.addAll(deviceBonded)
        onDeviceAvailable.invoke(mDevices)
    }

    // add new device not bonded to buffer
    private fun add(device: BluetoothDevice) {
        val deviceDistinct = mDevices.filter { it.address != device.address }
        mDevices = arrayListOf()
        mDevices.addAll(deviceDistinct)

        val newPrinter = PrinterUiModel(address = device.address, name = device.name.orEmpty())
        mDevices.add(newPrinter)

        onDeviceAvailable.invoke(mDevices)
    }


    fun startDiscovery() {
        // init bluetooth adapter
        initBluetoothAdapter()

        // If we're already discovering, stop it
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }

        bluetoothAdapter?.startDiscovery()
    }
}