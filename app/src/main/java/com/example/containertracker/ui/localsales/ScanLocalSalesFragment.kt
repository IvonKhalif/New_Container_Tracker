package com.example.containertracker.ui.localsales

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.databinding.FragmentScanLocalSalesBinding
import com.example.containertracker.databinding.InputVoyageLocalSalesDialogBinding
import com.example.containertracker.ui.flexi.ScanFlexiFragment
import com.example.containertracker.ui.history.detail.HistoryDetailActivity
import com.example.containertracker.ui.home.containercondition.ContainerConditionBottomSheet
import com.example.containertracker.ui.home.containercondition.models.ContainerConditionParam
import com.example.containertracker.ui.main.MainViewModel
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.response.GenericErrorResponse
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanLocalSalesFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private val viewModel: ScanLocalSalesViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModels()
    lateinit var binding: FragmentScanLocalSalesBinding

    private var scannerView: ZXingScannerView? = null

    // keep camera stop while container input
    private var isProgressInput = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanLocalSalesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainView()
        observer()
    }

    private fun mainView() {
        binding.apply {
            inputContainerCode.enableDrawableLeft(false)
            buttonSubmitContainer.setOnClickListener {
                viewModel.containerCode.value = binding.inputContainerCode.getTextInputSearch()
                viewModel.getContainer(viewModel.containerCode.value.orEmpty(),flag = FlagScanEnum.INPUT)
            }
            initScanner()
        }
    }

    private fun observer() {
        viewModel.apply {
            containerLiveData.observe(viewLifecycleOwner, ::handleContainerScan)

            loadingState.observe(viewLifecycleOwner) {
                handleLoadingWidget(it)
            }

            serverErrorState.observe(viewLifecycleOwner) {
                handleErrorWidget(it)
                isProgressInput = false
                resumeCamera()
            }

            networkErrorState.observe(viewLifecycleOwner) {
                showErrorMessage(it)
                isProgressInput = false
                resumeCamera()
            }

            soNumberList.observeNonNull(viewLifecycleOwner, ::handleSONumber)
        }
    }

    private fun initScanner() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView = ZXingScannerView(requireContext())
        scannerView?.setAutoFocus(true)
        binding.containerScanner.addView(scannerView)
        resumeCamera()
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
            viewModel.getContainer(resultString,flag = FlagScanEnum.SCAN)
        }
    }

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@ScanLocalSalesFragment)
        scannerView?.startCamera()
    }

    private fun pauseCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.stopCamera()
    }

    private fun handleSONumber(list: List<SalesOrderNumber>) {
        mainViewModel.soNumberListLiveData.value = list
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse?) {
        showErrorMessage(genericErrorResponse?.status ?: "Terjadi masalah pada server")
    }

    private fun handleContainerScan(container: Container) {
        if (container.id.isNotBlank())
            if (mainViewModel.locationLiveData.value?.id == mainViewModel.locationListLiveData.value?.first()?.id
                && container.voyageIdIn.isNullOrBlank()) {
                showVoyageIdField(container, "in")
            } else if (mainViewModel.locationLiveData.value?.id == mainViewModel.locationListLiveData.value?.last()?.id) {
                showVoyageIdField(container, "out")
            } else {
                showVoyageIdField(container, "in")
//                showContainerCondition(container)
            }
    }

    private fun showVoyageIdField(container: Container, status: String = "") {
        val dialog = Dialog(requireContext())

        val bindingToast: InputVoyageLocalSalesDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.input_voyage_local_sales_dialog, null, false
        )
        bindingToast.viewModel = viewModel
        bindingToast.lifecycleOwner = activity

        viewModel.voyageId.value = if (status == "out") container.voyageIdOut
        else container.voyageIdIn
        viewModel.voyageIdIn.value = container.voyageIdIn

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.buttonSubmit.setOnClickListener {
            if (status == "in") {
                viewModel.voyageIdIn.value = viewModel.voyageId.value
            } else if (status == "out") {
                viewModel.voyageIdOut.value = viewModel.voyageId.value
            }
            showContainerCondition(container)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showContainerCondition(container: Container) {
        val location = mainViewModel.locationLiveData.value
        val voyageIdIn = viewModel.voyageIdIn.value
        val voyageIdOut = viewModel.voyageIdOut.value
        ContainerConditionBottomSheet.newInstance(
            ContainerConditionParam(
                container = container,
                location = location,
                voyageIdOut = voyageIdOut,
                voyageIdIn = voyageIdIn,
                isLocalSales = true
            )
        ).apply {
            isCancelable = false

            onSaveLocalSalesDone = {
                isProgressInput = false
                handleStatusSaved(it)
                dismissAllowingStateLoss()
            }

            onClose = {
                isProgressInput = false
                resumeCamera()
            }
        }.also {
            it.show(parentFragmentManager, "tag")
        }
    }

    private fun handleStatusSaved(dataSaved: SaveLocalSalesRequest) {
        goToHistoryDetail(dataSaved = dataSaved)
        showSuccessMessage("Success Save")
    }

    private fun goToHistoryDetail(dataSaved: SaveLocalSalesRequest) {
        val container = viewModel.containerLiveData.value
        val intent = Intent(activity, HistoryDetailActivity::class.java)
        intent.putExtra(ExtrasConstant.EXTRA_CONTAINER_DATA, container)
        intent.putExtra(ExtrasConstant.EXTRA_VOYAGE_ID_IN, dataSaved.voyageIdIn)
        intent.putExtra(ExtrasConstant.EXTRA_VOYAGE_ID_OUT,  dataSaved.voyageIdOut)
        intent.putExtra(
            ExtrasConstant.EXTRA_SO_NUMBER,
            dataSaved.soNumber.orEmpty()
        )
        startActivity(intent)
    }
}