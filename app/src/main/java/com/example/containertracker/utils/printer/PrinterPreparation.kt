package com.example.containertracker.utils.printer

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResult

/**
 * Printer preparation like permission, hardware enable etc
 */
class PrinterPreparation(private val context: Context?) {
    // printer on ready callback
    var onPrinterReady: () -> Unit = { }
    // printer need permission callback
    var onNeedPermission: (permissions: Array<String>) -> Unit = {}
    // open setting
    var onOpenSetting: (Intent) -> Unit = {}

    private var launchRequestCode = -1

    companion object {

        const val REQUEST_BLUETOOTH_SETTING = 10
        const val REQUEST_LOCATION_SETTING = 10

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
            )
        }
    }

    /**
     * Prepared bluetooth device
     */
    fun start() {
        onNeedPermission.invoke(permissions)
    }

    /**
     * Checking hardware for prepare print
     */
    private fun prepareHardware() {
        val adapter = (context?.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val locationEnable = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (adapter == null) {
            // handleError(Throwable(owner.getString(R.string.device_not_support_bluetooth)))
        } else {
            if (!adapter.isEnabled) {
                settingBluetooth()
                return
            }
            // GPS disable, and user suggest navigate to setting GPS menu
            if (locationEnable == false) {
                settingLocation()
                return
            }

            onPrinterReady.invoke()
        }
    }

    /**
     * go to setting bluetooth if location is disable
     */
    private fun settingBluetooth() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        launchRequestCode = REQUEST_BLUETOOTH_SETTING
        onOpenSetting.invoke(intent)
    }

    /**
     * go to setting location if gps not active
     */
    private fun settingLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        launchRequestCode = REQUEST_LOCATION_SETTING
        onOpenSetting.invoke(intent)
    }

    fun onPermissionForResult(permissions: Map<String, Boolean>) {
        // filter permission not granted is empty
        val isAllGranted = permissions.filter { !it.value }.isEmpty()

        if (isAllGranted) {
            prepareHardware()
        } else {
            // show error
        }
    }

    fun onSettingForResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (launchRequestCode == REQUEST_BLUETOOTH_SETTING) {
                prepareHardware()
            } else if (launchRequestCode == REQUEST_LOCATION_SETTING) {
                prepareHardware()
            }
        }
    }
}