package com.example.containertracker.ui.isotank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.FragmentScanIsotankBinding
import com.example.containertracker.ui.isotank.form.IsoTankFormBottomSheet
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanIsotankFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    companion object {
        var isProgressInput = false
    }

    private val viewModel: ScanIsotankViewModel by viewModel()
    lateinit var binding: FragmentScanIsotankBinding

    private var scannerView: ZXingScannerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanIsotankBinding.inflate(inflater, container, false)
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
            it.loadingState.observe(viewLifecycleOwner) { isLoading ->
                handleLoadingWidget(isLoading)
            }
            it.serverErrorState.observe(viewLifecycleOwner) { error ->
                handleErrorServerWidget(error)
                isProgressInput = false
                resumeCamera()
            }
            it.networkErrorState.observe(viewLifecycleOwner) { error ->
                showErrorMessage(error)
                isProgressInput = false
                resumeCamera()
            }
        }
    }

    private fun mainView() {
        binding.apply {
            inputContainerCode.enableDrawableLeft(false)
            buttonSubmitContainer.setOnClickListener {
                viewModel.containerCode.value = binding.inputContainerCode.getTextInputSearch()
                viewModel.getContainer(viewModel.containerCode.value.orEmpty())
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
        showIsoTankForm(container)
    }

    private fun showIsoTankForm(container: Container) {
        IsoTankFormBottomSheet.newInstance(
            container = container
        ).apply {
            isCancelable = false

            onDone = {
                isProgressInput = false
                dismissAllowingStateLoss()
                resumeCamera()
            }

            onClose = {
                isProgressInput = false
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
            viewModel.getContainer(resultString)
        }
    }

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@ScanIsotankFragment)
        scannerView?.startCamera()
    }

    private fun pauseCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.stopCamera()
    }
}