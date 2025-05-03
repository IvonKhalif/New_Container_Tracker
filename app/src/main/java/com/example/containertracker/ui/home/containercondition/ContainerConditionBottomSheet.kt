package com.example.containertracker.ui.home.containercondition

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.base.useContext
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.databinding.BottomSheetContainerConditionBinding
import com.example.containertracker.ui.home.containercondition.adapter.ContainerConditionImageAdapter
import com.example.containertracker.ui.home.containercondition.models.ContainerConditionParam
import com.example.containertracker.ui.home.containercondition.models.ContainerImageUiModel
import com.example.containertracker.ui.home.salesordernumber.SalesOrderNumberBottomSheet
import com.example.containertracker.ui.home.selectcondition.SelectConditionBottomSheet
import com.example.containertracker.ui.main.MainViewModel
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.ui.media.select_image.SelectImageBottomSheet
import com.example.containertracker.utils.constants.ContainerConstant
import com.example.containertracker.utils.enums.ConditionEnum
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.utils.enums.PosEnum
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.example.containertracker.utils.enums.getIcon
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.extension.showImmediately
import com.example.containertracker.utils.media.FileUtil
import com.example.containertracker.utils.pickimage.PickImageUtils
import com.example.containertracker.widget.DatePickerWidget
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate

class ContainerConditionBottomSheet : BaseBottomSheet() {

    private lateinit var binding: BottomSheetContainerConditionBinding

    private val viewModel: ContainerConditionViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()

    //container image adapter
    private val containerImageAdapter by lazy {
        ContainerConditionImageAdapter {
            viewModel.onItemImageListClick(item = it)
        }
    }

    // get container data
    private val param by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                ARG_CONTAINER_PARAM,
                ContainerConditionParam::class.java
            )
        } else {
            arguments?.getParcelable<ContainerConditionParam>(ARG_CONTAINER_PARAM)
        }
    }

    // callback to submit
    var onDone: (SaveContainerHistoryRequest) -> Unit = {}
    var onSaveLocalSalesDone: (SaveLocalSalesRequest) -> Unit = {}

    var onClose: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_container_condition,
            container,
            false
        )
        binding.vm = viewModel
        binding.lifecycleOwner = parentFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        param?.let {
            viewModel.onSetupData(it)
        }

        initView()
        observer()
    }

    private fun observer() {
        viewModel.apply {
            containerData.observe(viewLifecycleOwner) {
                binding.headerBasicTextTitleSubheadline.text = it.codeContainer
            }

            backConditionData.observe(viewLifecycleOwner) {
                updateSidesCondition(it, ContainerSidesEnum.BACK)
            }
            doorConditionData.observe(viewLifecycleOwner) {
                updateSidesCondition(it, ContainerSidesEnum.DOOR)
            }
            upperConditionData.observe(viewLifecycleOwner) {
                updateSidesCondition(it, ContainerSidesEnum.ROOF)
            }
            floorConditionData.observe(viewLifecycleOwner) {
                updateSidesCondition(it, ContainerSidesEnum.FLOOR)
            }
            leftConditionData.observe(viewLifecycleOwner) {
                updateSidesCondition(it, ContainerSidesEnum.LEFT)
            }
            rightConditionData.observe(viewLifecycleOwner) {
                updateSidesCondition(it, ContainerSidesEnum.RIGHT)
            }
            salesOrderNumberData.observe(viewLifecycleOwner, ::handleSONumber)

            containerImageVisibilityState.observe(viewLifecycleOwner) {
                binding.groupContainerImages.isVisible = it
            }

            imageList.observe(viewLifecycleOwner) {
                binding.rvContainerImages.post {
                    containerImageAdapter.submitList(it)
                }
            }

            openMediaState.observe(viewLifecycleOwner) {
                showMediaDialog(data = it)
            }

            openImagePreviewState.observe(viewLifecycleOwner) {
                showImagePreview(data = it)
            }

            loadingState.observe(viewLifecycleOwner) {
                handleLoadingWidget(it)
            }

            networkErrorState.observe(viewLifecycleOwner) {
                showErrorMessage(it)
            }

            serverErrorState.observe(viewLifecycleOwner) {
                showErrorMessage(it.status.orEmpty())
            }

            submitState.observe(viewLifecycleOwner) {
                onDone.invoke(it)
                dismissAllowingStateLoss()
            }

            submitLocalSalesState.observe(viewLifecycleOwner) {
                onSaveLocalSalesDone.invoke(it)
                dismissAllowingStateLoss()
            }

            manufactureDate.observe(viewLifecycleOwner) {
                checkButtonSubmitEnable()
            }

            manufactureDateDisplay.observe(viewLifecycleOwner) {
                onManufactureDateSelected(it)
            }

            tarraWeight.observe(viewLifecycleOwner) {
                checkButtonSubmitEnable()
            }
        }
    }

    private fun onManufactureDateSelected(date: String?) {
        date?.let { binding.dropManufactureDate.setText(it) }
    }

    private fun handleSONumber(soNumber: String?) {
        if (soNumber.isNullOrBlank())
            binding.soNumber.setTextForHint(getString(R.string.general_action_select))
        else
            binding.soNumber.setText(soNumber)
    }

    private fun updateSidesCondition(
        conditionEnum: ConditionEnum?,
        containerSide: ContainerSidesEnum
    ) {
        if (conditionEnum == null) binding.rightCondition.setText(getString(R.string.general_action_select))
        when (containerSide) {
            ContainerSidesEnum.RIGHT -> conditionEnum?.let {
                binding.rightCondition.setDrawableEnd(it.getIcon())
                binding.rightCondition.setText(it.type)
            }

            ContainerSidesEnum.LEFT -> conditionEnum?.let {
                binding.leftCondition.setDrawableEnd(it.getIcon())
                binding.leftCondition.setText(it.type)
            }

            ContainerSidesEnum.DOOR -> conditionEnum?.let {
                binding.doorCondition.setDrawableEnd(it.getIcon())
                binding.doorCondition.setText(it.type)
            }

            ContainerSidesEnum.BACK -> conditionEnum?.let {
                binding.backCondition.setDrawableEnd(it.getIcon())
                binding.backCondition.setText(it.type)
            }

            ContainerSidesEnum.ROOF -> conditionEnum?.let {
                binding.roofCondition.setDrawableEnd(it.getIcon())
                binding.roofCondition.setText(it.type)
            }

            ContainerSidesEnum.FLOOR -> conditionEnum?.let {
                binding.floorCondition.setDrawableEnd(it.getIcon())
                binding.floorCondition.setText(it.type)
            }
            else -> {}
        }
    }

    private fun initView() = with(binding) {
        // setup behavior bottom sheet
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.behavior.skipCollapsed = true
                sheet.parent.requestLayout()
            }
        }

        backCondition.setTitle(ContainerConstant.BACK)
        backCondition.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.BACK)
        }

        doorCondition.setTitle(ContainerConstant.DOOR)
        doorCondition.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.DOOR)
        }

        roofCondition.setTitle(ContainerConstant.ROOF)
        roofCondition.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.ROOF)
        }

        floorCondition.setTitle(ContainerConstant.FLOOR)
        floorCondition.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.FLOOR)
        }

        leftCondition.setTitle(ContainerConstant.LEFT)
        leftCondition.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.LEFT)
        }

        rightCondition.setTitle(ContainerConstant.RIGHT)
        rightCondition.onClickDropDownListener {
            selectCondition(ContainerSidesEnum.RIGHT)
        }

        soNumber.setTitle(getString(R.string.so_number_label))
        soNumber.isVisible = mainViewModel.locationLiveData.value?.type == "OUT"
        soNumber.setOnClickListener {
            selectSONumber()
        }

        dropManufactureDate.setTitle("Manufacture Date")
        dropManufactureDate.onClickDropDownListener {
            val datePicker = DatePickerWidget()
            datePicker.bind(viewModel.manufactureDate)
            datePicker.show(childFragmentManager, datePicker.tag)
        }

        btnClose.setOnClickListener {
            onClose.invoke()
            dismissAllowingStateLoss()
        }

        buttonSubmit.setOnClickListener {
            viewModel.onSubmit(param?.isLocalSales.orFalse(), param?.isContainerLaden.orFalse())
        }

        // setup recycler view
        rvContainerImages.adapter = containerImageAdapter
        rvContainerImages.layoutManager = GridLayoutManager(
            requireContext(),
            3
        )

        param?.let {
            inputTarraWeight.isVisible =
                it.location?.id == PosEnum.POS2.posId.toString() &&
                        viewModel.userData.value?.departmentId.orEmpty() != RoleAccessEnum.LOCALSALES.value

            dropManufactureDate.setRequired(
                it.location?.id == PosEnum.POS2.posId.toString() &&
                        viewModel.userData.value?.departmentId.orEmpty() != RoleAccessEnum.LOCALSALES.value
            )

            inputTarraWeight.setRequired(
                it.location?.id == PosEnum.POS2.posId.toString() &&
                        viewModel.userData.value?.departmentId.orEmpty() != RoleAccessEnum.LOCALSALES.value
            )
        }
    }

    fun checkButtonSubmitEnable() {
        viewModel.apply {
            if (param?.location?.id == PosEnum.POS2.posId.toString() &&
                viewModel.userData.value?.departmentId.orEmpty() != RoleAccessEnum.LOCALSALES.value
            ) {
                binding.buttonSubmit.isEnabled =
                    !tarraWeight.value.isNullOrBlank() && (manufactureDate.value != null || manufactureDate.value != LocalDate.now())
            }
        }
    }

    /**
     * show select condition bottom sheet
     */
    private fun selectCondition(type: ContainerSidesEnum) {
        val selectPostBottomSheet = SelectConditionBottomSheet { condition ->
            viewModel.onSelectConditionChanged(side = type, conditionEnum = condition)
        }
        selectPostBottomSheet.show(childFragmentManager, selectPostBottomSheet.tag)
    }

    /**
     * show select SO number bottom sheet
     */
    private fun selectSONumber() {
        val bottomSheet = SalesOrderNumberBottomSheet {
            viewModel.onSalesOrderChanged(it)
        }
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    /**
     * show media bottom sheet
     */
    private fun showMediaDialog(data: ContainerImageUiModel) {
        val saveImageToDirectory =
            param?.location?.id == PosEnum.POS1.posId.toString() || param?.location?.id == PosEnum.POS7.posId.toString()
        showImmediately(childFragmentManager, "show_media") {
            SelectImageBottomSheet.newInstance(saveImageToDirectory).apply {
                onImageSelected = {
                    this@ContainerConditionBottomSheet.compressImage(
                        data,
                        PickImageUtils.uri
                    )
                }
            }
        }
    }

    private fun compressImage(data: ContainerImageUiModel, uri: Uri?) =
        useContext { usableContext ->
            val file = uri?.let { FileUtil.from(requireContext(), it) }
            viewModel.showLoadingWidget()
            CoroutineScope(Dispatchers.Main).launch {
                val compressedImageFile = file?.let {
                    Compressor.compress(usableContext, it) {
                        resolution(1280, 720)
                        quality(75)
                        format(Bitmap.CompressFormat.PNG)
//                    quality(80)
//                    size(1_048_576) // 1 MB
                    }
                }
                viewModel.hideLoadingWidget()
                compressedImageFile?.let {
                    // result can bigger from original

                    viewModel.onImageSelected(
                        position = data.position,
                        file = if (file.length() < compressedImageFile.length()) file else it
                    )
                }
            }
        }

    /**
     * show image preview
     */
    private val imagePreviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.onImagePreviewResult(resultCode = it.resultCode, intent = it.data) }

    /**
     * call image preview activity with data parcel
     */
    private fun showImagePreview(data: ContainerImageUiModel) {
        ImagePreviewActivity.open(
            context = requireContext(),
            launcher = imagePreviewLauncher,
            filePath = data.imageFilePath.orEmpty()
        )
    }

    companion object {
        private const val ARG_CONTAINER_PARAM = "ARG_CONTAINER_PARAM"
        private const val ARG_CONTAINER_DATA = "container_data"
        private const val ARG_LOCATION = "location"
        private const val ARG_VOYAGE_IN = "voyage_in"
        private const val ARG_VOYAGE_OUT = "voyage_out"
        private const val ARG_FROM_LOCAL_SALES = "FROM_LOCAL_SALES"

        fun newInstance(
            containerParam: ContainerConditionParam
        ): ContainerConditionBottomSheet {
            val sheet = ContainerConditionBottomSheet()

            sheet.arguments = Bundle().apply {
                putParcelable(ARG_CONTAINER_PARAM, containerParam)
            }

            return sheet
        }
    }
}