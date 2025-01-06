package com.example.containertracker.utils.enums

import android.Manifest
import com.example.containertracker.R

enum class PermissionEnum(val permission:String, val code: Int, val title:Int) {
    POST_NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS, 599, R.string.permission_type_post_notification),
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, 600,R.string.permission_type_access_coarse_location),
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, 601,R.string.permission_type_access_fine_location),
    READ_CONTACTS(Manifest.permission.READ_CONTACTS, 602,R.string.permission_type_read_contacts),
    CALL_PHONE(Manifest.permission.CALL_PHONE, 603,R.string.permission_type_call_phone),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, 604,R.string.permission_type_read_phone_state),
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE, 605,R.string.permission_type_read_external_storage),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, 606,R.string.permission_type_write_external_storage),
    CAMERA(Manifest.permission.CAMERA, 607,R.string.permission_type_camera),
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO, 608,R.string.permission_type_microphone),
    UNKNOWN("UNKNOWN", 111,R.string.general_all);

    companion object {
        fun fromPermission(permission: String) = PermissionEnum.values().firstOrNull { it.permission.equals(permission, ignoreCase = true) } ?: UNKNOWN
    }
}