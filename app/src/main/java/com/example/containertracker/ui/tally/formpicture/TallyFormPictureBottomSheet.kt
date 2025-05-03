package com.example.containertracker.ui.tally.formpicture

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.base.useContext
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.databinding.BottomSheetTallyFormPictureBinding
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.ui.media.select_image.SelectImageBottomSheet
import com.example.containertracker.ui.seal.ScanSealFragment
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_TALLY_SHEET_DATA
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.extension.showImmediately
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TallyFormPictureBottomSheet : BaseBottomSheet() {

    private lateinit var binding: BottomSheetTallyFormPictureBinding
    private val viewModel: TallyFormPictureViewModel by viewModel()

    private val tallySheet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_TALLY_SHEET_DATA, TallySheetDetailResponse::class.java)
        } else {
            arguments?.getParcelable(EXTRA_TALLY_SHEET_DATA)
        }
    }

    private val genericImageAdapter by lazy {
        GenericSelectImageAdapter {
            viewModel.onItemImageListClick(item = it)
        }
    }

    private val imagePreviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.onImagePreviewResult(resultCode = it.resultCode, intent = it.data) }

    var onSubmit: () -> Unit = {}
    var onClose: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetTallyFormPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    private fun initObserver() {
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
            tallySheetData.observe(viewLifecycleOwner) {
                viewModel.renderPhotoFromServer(it)
            }
        }
    }

    private fun initView() = with(binding){
        // First Init Data
        viewModel.setImageContainer()
        tallySheet?.let { viewModel.initDataTally(it) }
        // setup behavior bottom sheet
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.behavior.skipCollapsed = true
                sheet.parent.requestLayout()
            }

            rvContainerImages.adapter = genericImageAdapter
            rvContainerImages.layoutManager = GridLayoutManager(
                requireContext(),
                5
            )

            btnClose.setOnClickListener {
                onClose.invoke()
                dismissAllowingStateLoss()
            }

            buttonSubmit.setOnClickListener {
                onSubmit.invoke()
                dismissAllowingStateLoss()
            }
        }
    }

    private fun handleImageList(list: List<GenericSelectImageUiModel>?) {
        binding.rvContainerImages.post {
            genericImageAdapter.submitList(list)
        }
    }

    private fun showMediaDialog(data: GenericSelectImageUiModel) {
        showImmediately(childFragmentManager, "show_media") {
            SelectImageBottomSheet.newInstance().apply {
                onImageSelected = {
                    this@TallyFormPictureBottomSheet.compressImage(data, PickImageUtils.uri)
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
        fun newInstance(tallySheetData: TallySheetDetailResponse?) =
            TallyFormPictureBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_TALLY_SHEET_DATA, tallySheetData)
                }
            }
    }
}