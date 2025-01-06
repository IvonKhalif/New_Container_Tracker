package com.example.containertracker.ui.scanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.ActivityScannerBinding
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_CONTAINER_DATA
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.response.GenericErrorResponse
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private lateinit var binding: ActivityScannerBinding
    private val viewModel: ScannerViewModel by viewModel()
    private lateinit var scannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.scanner_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initScannerView()
        observer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun observer() {
        viewModel.apply {
            containerLiveData.observe(this@ScannerActivity, ::handleContainerScan)
            loadingWidgetLiveData.observeNonNull(this@ScannerActivity, { handleLoadingWidget(it) })
            genericErrorLiveData.observeNonNull(this@ScannerActivity, ::handleErrorWidget)
        }
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse) {
        showErrorMessage(genericErrorResponse.status.toString())
        scannerView.resumeCameraPreview(this)
    }

    private fun handleContainerScan(container: Container) {
        if (container.id.isNotBlank())
            resultData(container)
    }

    private fun resultData(container: Container) {
        val intent = Intent()
        intent.putExtra(EXTRA_CONTAINER_DATA, container)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun initScannerView() {
        scannerView = ZXingScannerView(this)
        scannerView.setAutoFocus(true)
        scannerView.setResultHandler(this)
        binding.containerScanner.addView(scannerView)
    }

    override fun handleResult(value: Result?) {
        val result = value.toString()
        if (result.isNotBlank()) {
            viewModel.getContainer(result)
        } else {
            scannerView.resumeCameraPreview(this)
        }
    }

    override fun onStart() {
        scannerView.startCamera()
        super.onStart()
    }

    override fun onPause() {
        scannerView.stopCamera()
        super.onPause()
    }

    override fun onResume() {
        scannerView.resumeCameraPreview(this)
        super.onResume()
    }
}