package com.example.containertracker.base

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.containertracker.utils.constants.ToastConstant
import com.example.containertracker.utils.response.GenericErrorResponse

abstract class BaseFragment: Fragment() {
    protected val baseActivity: BaseActivity
        get() = requireActivity() as BaseActivity

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

    fun handleErrorServerWidget(genericErrorResponse: GenericErrorResponse?) {
        val descriptionError = if (genericErrorResponse?.desc.isNullOrBlank()) genericErrorResponse?.status.orEmpty()
        else genericErrorResponse?.desc
        showErrorMessage(descriptionError ?: "Terjadi masalah pada server")
    }

}