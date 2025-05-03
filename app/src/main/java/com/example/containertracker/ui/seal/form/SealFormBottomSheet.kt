package com.example.containertracker.ui.seal.form

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.base.useContext
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.BottomSheetSealFormBinding
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.ui.media.select_image.SelectImageBottomSheet
import com.example.containertracker.ui.seal.ScanSealFragment
import com.example.containertracker.ui.seal.preview.SealPreviewActivity
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.extension.isNullOrZero
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.extension.orZero
import com.example.containertracker.utils.extension.showImmediately
import com.example.containertracker.utils.extension.toArrayList
import com.example.containertracker.utils.media.FileUtil
import com.example.containertracker.utils.pickimage.PickImageUtils
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageAdapter
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SealFormBottomSheet : BaseBottomSheet() {

    private lateinit var binding: BottomSheetSealFormBinding
    private val viewModel: SealFormViewModel by viewModel()

    // get container data
    private val container by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_CONTAINER_DATA, Container::class.java)
        } else {
            arguments?.getParcelable<Container>(ARG_CONTAINER_DATA)
        }
    }

    private val genericImageAdapter by lazy {
        GenericSelectImageAdapter {
            viewModel.onItemImageListClick(item = it)
        }
    }

    private val isLocalSales by lazy {
        arguments?.getBoolean(ExtrasConstant.EXTRA_IS_LOCAL_SALES, false)
    }

    private val imagePreviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.onImagePreviewResult(resultCode = it.resultCode, intent = it.data) }

    // callback to submit
    var onNext: () -> Unit = {}
    var onClose: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_seal_form,
            container,
            false
        )
        binding.viewModelBinding = viewModel
        binding.lifecycleOwner = parentFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainView()
        observer()
    }

    private fun observer() {
        viewModel.apply {
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
            imageList.observe(viewLifecycleOwner) {
                handleImageList(it)
            }
        }
    }

    private fun handleImageList(list: List<GenericSelectImageUiModel>?) {
        val minPhoto = if (container?.maxPhoto.orZero() >= 8) 4 else 2
        binding.rvContainerImages.post {
            genericImageAdapter.submitList(list)
        }
        binding.buttonSubmit.isEnabled = (list?.filterNot {
            it.imageFilePath.isNullOrBlank()
        }?.size ?: 0) >= minPhoto
    }

    private fun mainView() = with(binding) {
        // setup behavior bottom sheet
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.behavior.skipCollapsed = true
                sheet.parent.requestLayout()
            }
        }

        btnClose.setOnClickListener {
            onClose.invoke()
            dismissAllowingStateLoss()
        }

        buttonSubmit.setOnClickListener {
            goToPreview()
        }

        container?.let { data ->
            if (data.maxPhoto.isNullOrZero()) viewModel.setImageContainer(8)
            else data.maxPhoto?.let { viewModel.setImageContainer(it) }
            // setup recycler view
            rvContainerImages.adapter = genericImageAdapter
            rvContainerImages.layoutManager = GridLayoutManager(
                requireContext(),
                4
            )
        }
    }

    private fun goToPreview() {
        val imageList = viewModel.imageList.value.orEmpty().toArrayList()
        val intent = Intent(activity, SealPreviewActivity::class.java)
        intent.putExtra(ExtrasConstant.EXTRA_IMAGE_LIST_DATA, imageList)
        intent.putExtra(ExtrasConstant.EXTRA_CONTAINER_DATA, container)
        intent.putExtra(ExtrasConstant.EXTRA_IS_LOCAL_SALES, isLocalSales)
        resultLauncher.launch(intent)
    }

    /**
     * show media bottom sheet
     */
    private fun showMediaDialog(data: GenericSelectImageUiModel) {
        showImmediately(childFragmentManager, "show_media") {
            SelectImageBottomSheet.newInstance().apply {
                onImageSelected = {
                    this@SealFormBottomSheet.compressImage(data, PickImageUtils.uri)
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    private fun compressImage(data: GenericSelectImageUiModel, uri: Uri?) = useContext { usableContext ->
        val file = uri?.let { FileUtil.from(requireContext(), it) }
        viewModel.showLoadingWidget()
        CoroutineScope(Dispatchers.Main).launch {
            val compressedImageFile = file?.let {
                Compressor.compress(usableContext, it) {
                    resolution(1280, 720)
                    quality(75)
                    format(Bitmap.CompressFormat.PNG)
                }
            }
            viewModel.hideLoadingWidget()
            compressedImageFile?.let {
                viewModel.onImageSelected(
                    position = data.position,
                    file = if (file.length() < compressedImageFile.length()) file else it
                )
            }
        }
    }

    /**
     * call image preview activity with data parcel
     */
    private fun showImagePreview(data: GenericSelectImageUiModel) {
        ImagePreviewActivity.open(
            context = requireContext(),
            launcher = imagePreviewLauncher,
            filePath = data.imageFilePath.orEmpty()
        )
    }

    override fun onResultData(result: Intent?) {
        super.onResultData(result)
        ScanSealFragment.isProgressInput = false
        val isSuccessSave = result?.getBooleanExtra(ExtrasConstant.EXTRA_SUCCESS_SAVE, false)
        if (isSuccessSave.orFalse()) dismiss()
    }

    companion object {
        private const val ARG_CONTAINER_DATA = "container_data"

        fun newInstance(
            container: Container,
            isLocalSales: Boolean
        ): SealFormBottomSheet {
            val sheet = SealFormBottomSheet()

            sheet.arguments = Bundle().apply {
                putParcelable(ARG_CONTAINER_DATA, container)
                putBoolean(ExtrasConstant.EXTRA_IS_LOCAL_SALES, isLocalSales)
            }

            return sheet
        }
    }
}