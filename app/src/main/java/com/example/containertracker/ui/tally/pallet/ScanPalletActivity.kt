package com.example.containertracker.ui.tally.pallet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.ActivityScanPalletBinding
import com.example.containertracker.databinding.FinishProcessTallyDialogBinding
import com.example.containertracker.ui.flexi.ScanFlexiFragment
import com.example.containertracker.ui.tally.pallet.formpallet.PalletFormBottomSheet
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.constants.ResultCode
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanPalletActivity : BaseActivity(), ZXingScannerView.ResultHandler {
    companion object {
        // keep camera stop while container input
        private var isProgressInput = false
    }

    private val viewModel: ScanPalletViewModel by viewModel()
    lateinit var binding: ActivityScanPalletBinding
    private var scannerView: ZXingScannerView? = null

    private val tallySheet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA, TallySheetDrafts::class.java)
        } else {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityScanPalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initScanner()
        initObserver()
    }

    private fun initView() = with(binding) {
        supportActionBar?.title = getString(R.string.tally_sheet_scan_pallet_id_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonSubmitContainer.setOnClickListener {
            showFinishProcessDialog()
        }
    }

    private fun initObserver() = viewModel.also {
        it.onSuccessScan.observeAction(this) {
            handleSuccess()
        }
        it.onSuccessSubmit.observeAction(this) {
            handleSubmit()
        }
        it.loadingState.observe(this) { isLoading ->
            handleLoadingWidget(isLoading)
        }
        it.serverErrorState.observe(this) { error ->
            handleErrorServerWidget(error)
            ScanFlexiFragment.isProgressInput = false
            resumeCamera()
        }
        it.networkErrorState.observe(this) { error ->
            showErrorMessage(error)
            ScanFlexiFragment.isProgressInput = false
            resumeCamera()
        }
    }

    private fun handleSuccess() {
        showSuccessMessage(getString(R.string.tally_sheet_success_scan_spm_message))
        showPalletForm()
    }

    private fun showPalletForm() {
        PalletFormBottomSheet.newInstance(
            palletData = null,
            tallySheetData = tallySheet
        ).also {
            it.show(supportFragmentManager, "tag")
        }
    }

    private fun handleSubmit() {
        val resultIntent = Intent()
        setResult(ResultCode.RESULT_SUCCESS_SUBMIT, resultIntent)
        finish()
    }

    private fun showFinishProcessDialog() {
        val dialog = Dialog(this)

        val bindingToast: FinishProcessTallyDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.finish_process_tally_dialog, null, false
        )

        bindingToast.lifecycleOwner = this

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.btnFinish.setOnClickListener {
            viewModel.finishProcess(tallySheet?.idTallySheet.orEmpty())
        }
        bindingToast.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initScanner() {
        scannerView = ZXingScannerView(this)
        scannerView?.setAutoFocus(true)
        scannerView?.setResultHandler(this)
        binding.containerScanner.addView(scannerView)
        resumeCamera()
    }

    override fun handleResult(result: Result?) {
        val resultString = result.toString()
        if (resultString.isNotBlank()) {
            isProgressInput = true
            pauseCamera()
            viewModel.scanPallet(resultString)
        }
    }

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@ScanPalletActivity)
        scannerView?.startCamera()
    }

    private fun pauseCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView = null
    }

    override fun onResume() {
        super.onResume()
        if (isProgressInput) {
            resumeCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseCamera()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}