package com.example.containertracker.ui.tally.spm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.ActivityScanSpmBinding
import com.example.containertracker.ui.flexi.ScanFlexiFragment
import com.example.containertracker.ui.tally.pallet.list.PalletActivity
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.constants.ResultCode
import com.example.containertracker.utils.enums.FlagScanEnum
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanSPMActivity : BaseActivity(), ZXingScannerView.ResultHandler {
    companion object {
        // keep camera stop while container input
        private var isProgressInput = false
    }

    private val viewModel: ScanSPMViewModel by viewModel()
    lateinit var binding: ActivityScanSpmBinding
    private var scannerView: ZXingScannerView? = null

    private val tallySheet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA, TallySheetDrafts::class.java)
        } else {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_spm)
        binding.viewModelBinding = viewModel
        binding.lifecycleOwner = this

        initView()
        initScanner()
        initObserver()
    }

    private fun initView() = with(binding) {
        supportActionBar?.title = getString(R.string.tally_sheet_scan_spm_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        inputContainerCode.enableDrawableLeft(false)
        buttonSubmitSpm.setOnClickListener {
            viewModel.scanSPM(viewModel.spmNumber.value.orEmpty(), idTallySheet = tallySheet?.idTallySheet.orEmpty(), flag = FlagScanEnum.INPUT)
        }
//        buttonScanPallet.setOnClickListener {
//            goToScanPallet()
//        }
    }

    private fun initObserver() = viewModel.also {
        it.onSuccessScan.observeAction(this) {
            handleSuccess()
        }
        it.loadingState.observe(this) { isLoading ->
            handleLoadingWidget(isLoading)
        }
        it.serverErrorState.observe(this) { error ->
            handleErrorServerWidget(error)
            isProgressInput = false
            resumeCamera()
        }
        it.networkErrorState.observe(this) { error ->
            showErrorMessage(error)
            isProgressInput = false
            resumeCamera()
        }
    }

    private fun handleSuccess() {
        showSuccessMessage(getString(R.string.tally_sheet_success_scan_spm_message))
        goToPalletList()
    }

    private fun goToPalletList() {
        isProgressInput = false
        val intent = Intent(this, PalletActivity::class.java)
        intent.putExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA, tallySheet)
        startForResult.launch(intent)
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
            viewModel.scanSPM(resultString, idTallySheet = tallySheet?.idTallySheet.orEmpty(), flag = FlagScanEnum.SCAN)
        }
    }

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@ScanSPMActivity)
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

    override fun onActivityResult(result: ActivityResult) {
        super.onActivityResult(result)
        if (result.resultCode == ResultCode.RESULT_SUCCESS_SUBMIT) {
            val resultIntent = Intent()
            setResult(ResultCode.RESULT_SUCCESS_SUBMIT, resultIntent)
            finish()
        }
    }
}