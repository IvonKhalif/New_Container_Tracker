package com.example.containertracker.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.containertracker.R
import com.example.containertracker.databinding.SquareToastFormatBinding
import com.example.containertracker.utils.constants.ToastConstant
import com.example.containertracker.utils.extension.showImmediately
import com.example.containertracker.widget.LoadingDialogWidget

abstract class BaseActivity : AppCompatActivity() {

    var loadingDialog: LoadingDialogWidget? = null

    fun showSuccessMessage(message: String) =
        customToast(baseContext, message, ToastConstant.CUSTOM_TOAST_SUCCESS)

    fun showErrorMessage(message: String) =
        customToast(this, message, ToastConstant.CUSTOM_TOAST_ERROR)

    fun showErrorMessage(throwable: Throwable) =
        customToast(this, throwable.message.orEmpty(), ToastConstant.CUSTOM_TOAST_ERROR)

    fun customToast(context: Context, message: String, flag: String, completion: () -> Unit = {}) {
        customToast(message, flag, completion = completion)
    }

    fun customToast(
        message: String,
        flag: String,
        yOffset: Int = 104,
        completion: () -> Unit = {}
    ) {
        val bindingToast: SquareToastFormatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.square_toast_format, null, false
        )

        bindingToast.lifecycleOwner = this

        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)

        when (flag) {
            ToastConstant.CUSTOM_TOAST_ERROR -> showRedSquareToast(
                bindingToast,
                this
            )
            ToastConstant.CUSTOM_TOAST_SUCCESS -> showBlueLiteSquareToast(
                bindingToast,
                this
            )
        }

        bindingToast.textToastMessage.text = message
        toast.view = bindingToast.root
        toast.setGravity(Gravity.TOP, 0, yOffset)

        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
            completion()
        }, 5000)
    }

    private fun showRedSquareToast(
        bindingToast: SquareToastFormatBinding,
        context: Context
    ) {
        bindingToast.textToastMessage.background =
            ContextCompat.getDrawable(
                context,
                R.drawable.square_background_with_solid_red50_color
            )
        bindingToast.textToastMessage.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_cross,
            0,
            0,
            0
        )
        bindingToast.textToastMessage.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.red_F44336
            )
        )
    }

    private fun showBlueLiteSquareToast(bindingToast: SquareToastFormatBinding, context: Context) {
        bindingToast.textToastMessage.background =
            ContextCompat.getDrawable(
                context,
                R.drawable.square_background_with_solid_blue50_color
            )
        bindingToast.textToastMessage.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_circle_check,
            0,
            0,
            0
        )
        bindingToast.textToastMessage.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.blue_lite_2196F3
            )
        )
    }

    fun handleLoadingWidget(isLoading: Boolean) {
        handleLoadingWidget(supportFragmentManager, isLoading)
    }

    fun handleLoadingWidget(fragmentManager: FragmentManager, isLoading: Boolean) {
        if (isLoading) showLoadingWidget(fragmentManager) else hideLoadingWidget()
    }

    private fun showLoadingWidget(fragmentManager: FragmentManager) {
        showImmediately(fragmentManager, "loading") {
            loadingDialog = LoadingDialogWidget.newInstance()
            loadingDialog!!
        }
    }

    private fun hideLoadingWidget() {
        loadingDialog?.dismissAllowingStateLoss()
    }
}