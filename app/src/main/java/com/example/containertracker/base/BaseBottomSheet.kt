package com.example.containertracker.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.containertracker.R
import com.example.containertracker.utils.constants.ToastConstant
import com.example.containertracker.utils.enums.PermissionEnum
import com.example.containertracker.utils.extension.orFalse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet: BottomSheetDialogFragment() {
    lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            onResultData(data)
        }
    }

    open fun onResultData(result: Intent?) {}

    fun showSuccessMessage(message: String) =
        activity?.let { (activity as? BaseActivity)?.customToast(it, message, ToastConstant.CUSTOM_TOAST_SUCCESS) }

    fun showErrorMessage(message: String) =
        activity?.let { (activity as? BaseActivity)?.customToast(it, message, ToastConstant.CUSTOM_TOAST_ERROR) }

    fun showErrorMessage(throwable: Throwable) =
        activity?.let { (activity as? BaseActivity)?.customToast(it, throwable.message.orEmpty(), ToastConstant.CUSTOM_TOAST_ERROR) }

    fun handleLoadingWidget(isLoading: Boolean) =
        activity?.let { (activity as? BaseActivity)?.handleLoadingWidget(childFragmentManager, isLoading) }

}

fun BaseBottomSheet.useContext(action:(context: Context) -> Unit){
    context?.let {
        action(it)
    }
}

fun BaseBottomSheet.runPermissionRequest(
    permissions: Array<String> = arrayOf(),
    onDenied: (Array<String>) -> Unit = {},
    onRational: (Array<String>) -> Unit = {},
    onGranted: (Array<String>) -> Unit = {}
) = useContext { usableContext ->
    data class PermissionStatus(
        val permission: String,
        val isGranted: Boolean,
        val isRational: Boolean,
        val isDenied: Boolean
    )
    permissions.map {
        val isPermissionGranted = isPermissionGranted(it, usableContext)
        val isRationale = shouldShowRequestPermissionRationale(it)
        PermissionStatus(
            permission = it,
            isGranted = isPermissionGranted,
            isRational = isRationale,
            isDenied = !isPermissionGranted && !isRationale
        )
    }.run {
        val granted = filter { p -> p.isGranted }.map { it.permission }.toTypedArray()
        val rational = filter { p -> p.isRational }.map { it.permission }.toTypedArray()
        val denied = filter { p -> p.isDenied }.map { it.permission }.toTypedArray()

        when {
            granted.size == this.size -> {
                onGranted(granted)
                dismissPermissionRationaleModal()
            }
            rational.isNotEmpty() -> {
                onRational(rational)
//                showPermissionRationaleModal(pagePermission)
            }
            denied.size == 1 && denied[0] == PermissionEnum.POST_NOTIFICATIONS.permission -> {
                onDenied(denied)
            }
            denied.isNotEmpty() -> {
                requestMultiplePermissions.launch(denied)
            }
            else -> Unit
        }
    }
}

fun Fragment.isPermissionGranted(permission: String, ctx: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        ctx,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.dismissPermissionRationaleModal() = try {
    childFragmentManager.findFragmentByTag("PERMISSION_RATIONALE_MODAL")?.dismissBottomSheetWidget()
} catch (e:java.lang.Exception){

}

fun Fragment.dismissBottomSheetWidget() = try {
    (parentFragment as? BottomSheetDialogFragment)?.dismiss()
} catch (e:Exception){
    /** handle error */
}

fun Fragment.showPermissionRationaleModal(pagePermission: List<PermissionEnum>,onDismissDialog: (() -> Unit)? = null, onSecondaryClick:() -> Unit = {}) {
    val tagDialog = "PERMISSION_RATIONALE_MODAL"
    if(childFragmentManager.findFragmentByTag(tagDialog)?.isVisible.orFalse()){
        return
    }
    openPermissionSettings()
}

fun Fragment.openPermissionSettings() {
    context?.let {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + it.packageName)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        it.startActivity(intent)
    }
}