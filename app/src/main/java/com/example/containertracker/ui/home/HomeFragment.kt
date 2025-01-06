package com.example.containertracker.ui.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.databinding.ContainerClosingDialogBinding
import com.example.containertracker.databinding.ContainerRejectionStatusDialogBinding
import com.example.containertracker.databinding.FragmentHomeBinding
import com.example.containertracker.databinding.InputVoyageDialogBinding
import com.example.containertracker.ui.history.detail.HistoryDetailActivity
import com.example.containertracker.ui.home.containercondition.ContainerConditionBottomSheet
import com.example.containertracker.ui.home.containercondition.models.ContainerConditionParam
import com.example.containertracker.ui.home.containerdefect.ContainerDefectFormBottomSheet
import com.example.containertracker.ui.main.MainViewModel
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_CONTAINER_DATA
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_CONTAINER_LADEN_FLAG
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_SO_NUMBER
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_VOYAGE_ID_IN
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_VOYAGE_ID_OUT
import com.example.containertracker.utils.enums.ContainerRejectionStatusEnum
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.enums.PosEnum
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.example.containertracker.utils.enums.TypeScanEnum
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.response.GenericErrorResponse
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private val viewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    private var scannerView: ZXingScannerView? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // keep camera stop while container input
    private var isProgressInput = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initScanner()
        observer()
    }

    private fun initView() {
        binding.inputContainerCode.enableDrawableLeft(false)
        binding.buttonSubmitContainer.setOnClickListener {
            viewModel.containerCode.value = binding.inputContainerCode.getTextInputSearch()
            viewModel.getContainer(flag = FlagScanEnum.INPUT)
        }

        binding.tvContainerLaden.setOnClickListener {
            viewModel.isContainerLaden.value = true
            binding.tvContainerLaden.setSelectedContainerType(true)
            binding.tvContainerExport.setSelectedContainerType(false)
        }
        binding.tvContainerExport.setOnClickListener {
            viewModel.isContainerLaden.value = false
            binding.tvContainerLaden.setSelectedContainerType(false)
            binding.tvContainerExport.setSelectedContainerType(true)
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
            isContainerLaden.observe(viewLifecycleOwner) {
                if (it != null)
                    mainViewModel.isContainerLaden.value = it
            }
        }
    }

    private fun handleContainerScan(container: Container) {
        val user = viewModel.userData.value
        if (container.id.isNotBlank())
            when {
                mainViewModel.locationLiveData.value?.id == PosEnum.POS2.posId.toString() &&
                        user?.departmentId.orEmpty() != RoleAccessEnum.LOCALSALES.value -> {
                    showRejectionDialog(container, "in")
                }
                mainViewModel.locationLiveData.value?.id == PosEnum.POS5.posId.toString() &&
                        user?.departmentId.orEmpty() != RoleAccessEnum.LOCALSALES.value -> {
                    showRejectionDialog(container, "in")
                }
                else -> {
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
            }
    }

    private fun showRejectionDialog(container: Container, status: String = "") {
        val dialog = Dialog(requireContext())

        val bindingToast: ContainerRejectionStatusDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.container_rejection_status_dialog, null, false
        )
        bindingToast.lifecycleOwner = activity

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.buttonContinue.setOnClickListener {
            viewModel.rejectionStatus.value = ContainerRejectionStatusEnum.RELEASE
            showVoyageIdField(container, status)
            dialog.dismiss()
        }
        bindingToast.buttonDefect.setOnClickListener {
            viewModel.rejectionStatus.value = ContainerRejectionStatusEnum.DEFECT
            showContainerDefectForm(container)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showVoyageIdField(container: Container, status: String = "") {
        val dialog = Dialog(requireContext())

        val bindingToast: InputVoyageDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.input_voyage_dialog, null, false
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
            if (viewModel.isContainerLaden.value.orFalse())
                showClosingContainerDialog(container)
            else
                showContainerCondition(container)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showContainerCondition(container: Container, containerLadenScanType: TypeScanEnum? = null) {
        val location = mainViewModel.locationLiveData.value
        val voyageIdIn = viewModel.voyageIdIn.value
        val voyageIdOut = viewModel.voyageIdOut.value
        val rejectionStatus = viewModel.rejectionStatus.value ?: ContainerRejectionStatusEnum.RELEASE
        ContainerConditionBottomSheet.newInstance(
            ContainerConditionParam(
                container = container,
                location = location,
                voyageIdOut = voyageIdOut,
                voyageIdIn = voyageIdIn,
                rejectionStatusEnum = rejectionStatus,
                containerDefectParam = viewModel.containerDefectParam,
                isContainerLaden = viewModel.isContainerLaden.value.orFalse(),
                containerLadenScanType = containerLadenScanType
            )
        ).apply {
            isCancelable = false

            onDone = {
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

    private fun handleSONumber(list: List<SalesOrderNumber>) {
        mainViewModel.soNumberListLiveData.value = list
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse?) {
        showErrorMessage(genericErrorResponse?.status ?: "Terjadi masalah pada server")
    }

    private fun handleStatusSaved(dataSaved: SaveContainerHistoryRequest) {
        goToHistoryDetail(dataSaved = dataSaved)
        showSuccessMessage("Success Save")
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
        _binding = null
    }

    private fun goToHistoryDetail(dataSaved: SaveContainerHistoryRequest) {
        val container = viewModel.containerLiveData.value
        val intent = Intent(activity, HistoryDetailActivity::class.java)
        intent.putExtra(EXTRA_CONTAINER_DATA, container)
        intent.putExtra(EXTRA_VOYAGE_ID_IN, dataSaved.voyageIdIn)
        intent.putExtra(EXTRA_VOYAGE_ID_OUT,  dataSaved.voyageIdOut)
        intent.putExtra(EXTRA_CONTAINER_LADEN_FLAG,  viewModel.isContainerLaden.value)
        intent.putExtra(
            EXTRA_SO_NUMBER,
            dataSaved.soNumber.orEmpty()
        )
        startActivity(intent)
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

    private fun resumeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.setResultHandler(this@HomeFragment)
        scannerView?.startCamera()
    }

    private fun pauseCamera() = lifecycleScope.launch(Dispatchers.Main) {
        scannerView?.stopCamera()
    }

    override fun handleResult(result: Result?) {
        val resultString = result.toString()
        if (resultString.isNotBlank()) {
            isProgressInput = true
            pauseCamera()
            viewModel.getContainer(resultString, FlagScanEnum.SCAN)
        }
    }

    private fun showContainerDefectForm(container: Container) {
        ContainerDefectFormBottomSheet.newInstance().apply {
            onNext = {
                isProgressInput = false
                viewModel.containerDefectParam = it
                showVoyageIdField(container)
                dismissAllowingStateLoss()
            }
        }.also {
            it.show(parentFragmentManager, "tag")
        }
    }

    private fun TextView.setSelectedContainerType(isSelected: Boolean) {
        context?.let { ctx ->
            val color = if (isSelected) R.color.primaryBlue else R.color.gray_ADABAB
            val background = if (isSelected) R.drawable.bg_rounded_capsule_outline_blue else R.drawable.bg_rounded_capsule_outline_grey
            val checkedIconRes = if (isSelected) {
                R.drawable.ic_check_blue_18dp
            } else 0
            this.setTextColor(ContextCompat.getColor(ctx, color))
            this.setBackgroundResource(background)
            this.setCompoundDrawablesWithIntrinsicBounds(checkedIconRes, 0, 0, 0)
        }
    }

    private fun showClosingContainerDialog(container: Container) {
        val dialog = Dialog(requireContext())

        val bindingToast: ContainerClosingDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.container_closing_dialog, null, false
        )
        bindingToast.lifecycleOwner = activity

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.buttonContinue.setOnClickListener {
            viewModel.rejectionStatus.value = ContainerRejectionStatusEnum.RELEASE
            showContainerCondition(container, TypeScanEnum.OPEN)
            dialog.dismiss()
        }
        bindingToast.buttonClosing.setOnClickListener {
            showContainerCondition(container, TypeScanEnum.CLOSING)
            dialog.dismiss()
        }

        dialog.show()
    }
}