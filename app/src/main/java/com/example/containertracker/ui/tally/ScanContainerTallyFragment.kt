package com.example.containertracker.ui.tally

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.FragmentScanContainerTallyBinding
import com.example.containertracker.ui.flexi.ScanFlexiFragment
import com.example.containertracker.ui.flexi.ScanFlexiFragment.Companion
import com.example.containertracker.ui.flexi.form.FlexiFormBottomSheet
import com.example.containertracker.utils.enums.FlagScanEnum
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanContainerTallyFragment : BaseFragment(), ZXingScannerView.ResultHandler {
    companion object {
        // keep camera stop while container input
        private var isProgressInput = false
    }

    private val viewModel: ScanContainerTallyViewModel by viewModel()
    lateinit var binding: FragmentScanContainerTallyBinding

    private var scannerView: ZXingScannerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScanContainerTallyBinding.inflate(inflater, container, false)
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
                ScanFlexiFragment.isProgressInput = false
                resumeCamera()
            }
            it.networkErrorState.observe(viewLifecycleOwner) {error ->
                showErrorMessage(error)
                ScanFlexiFragment.isProgressInput = false
                resumeCamera()
            }
        }
    }

    private fun mainView() {
        binding.apply {
            inputContainerCode.enableDrawableLeft(false)
            buttonSubmitContainer.setOnClickListener {
                viewModel.containerCode.value = binding.inputContainerCode.getTextInputSearch()
                viewModel.scanContainer(flag = FlagScanEnum.INPUT)
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
        showTallyForm(container)
    }

    private fun showTallyForm(container: Container) {
        FlexiFormBottomSheet.newInstance(
            container = container
        ).apply {
            isCancelable = false

            onNext = {
                ScanFlexiFragment.isProgressInput = false
//                handleStatusSaved(it)
                dismissAllowingStateLoss()
            }

            onClose = {
                ScanFlexiFragment.isProgressInput = false
                resumeCamera()
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
            viewModel.scanContainer(resultString, flag = FlagScanEnum.SCAN)
        }
    }

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@ScanContainerTallyFragment)
        scannerView?.startCamera()
    }

    private fun pauseCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.stopCamera()
    }
}