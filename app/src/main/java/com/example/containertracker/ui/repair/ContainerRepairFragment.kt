package com.example.containertracker.ui.repair

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.FragmentContainerRepairBinding
import com.example.containertracker.ui.flexi.ScanFlexiFragment.Companion.isProgressInput
import com.example.containertracker.ui.repair.form.ContainerRepairFormBottomSheet
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.enums.FlagScanEnum
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContainerRepairFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private val viewModel: ContainerRepairViewModel by viewModel()
    private lateinit var binding: FragmentContainerRepairBinding
    private var scannerView: ZXingScannerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContainerRepairBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        mainView()
    }

    private fun initObserver() {
        this.binding.viewModelBinding = viewModel.also {
            it.containerLiveData.observe(viewLifecycleOwner, ::handleContainerScan)
            it.loadingState.observe(viewLifecycleOwner) {isLoading ->
                handleLoadingWidget(isLoading)
            }
            it.serverErrorState.observe(viewLifecycleOwner) {error ->
                handleErrorServerWidget(error)
                isProgressInput = false
                resumeCamera()
            }
            it.networkErrorState.observe(viewLifecycleOwner) {error ->
                showErrorMessage(error)
                isProgressInput = false
                resumeCamera()
            }
            it.onSuccessSubmit.observeAction(this) {
                handleSuccess()
            }
        }
    }

    private fun mainView() {
        binding.apply {
            inputContainerCode.enableDrawableLeft(false)
            buttonSubmitContainer.setOnClickListener {
                viewModel.containerCode.value = binding.inputContainerCode.getTextInputSearch()
                viewModel.getContainer(viewModel.containerCode.value.orEmpty(), FlagScanEnum.INPUT)
            }
            initScanner()
        }
    }

    private fun initScanner() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView = ZXingScannerView(requireContext())
        scannerView?.setAutoFocus(true)
        binding.containerScanner.addView(scannerView)
        resumeCamera()
    }

    private fun handleContainerScan(container: Container) {
        ContainerRepairFormBottomSheet.newInstance().apply {
            onSubmitClicked = { list ->
                isProgressInput = false
                viewModel.saveContainerRepair(container, list)
                dismissAllowingStateLoss()
            }
        }.also {
            it.show(parentFragmentManager, "tag")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scannerView = null
    }

    override fun onResume() {
        super.onResume()
        if (!isProgressInput) {
            resumeCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseCamera()
    }

    override fun handleResult(result: Result?) {
        val resultString = result.toString()
        if (resultString.isNotBlank()) {
            isProgressInput = true
            pauseCamera()
            viewModel.getContainer(resultString, FlagScanEnum.SCAN)
        }
    }

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@ContainerRepairFragment)
        scannerView?.startCamera()
    }

    private fun pauseCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.stopCamera()
    }

    private fun handleSuccess() {
        showSuccessMessage(getString(R.string.scanner_success_save_data_label))
        resumeCamera()
    }

}