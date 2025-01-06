package com.example.containertracker.ui.home.containerdefect

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.base.useContext
import com.example.containertracker.databinding.BottomSheetContainerDefectFormBinding
import com.example.containertracker.ui.flexi.ScanFlexiFragment
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.ui.media.select_image.SelectImageBottomSheet
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.extension.showImmediately
import com.example.containertracker.utils.media.FileUtil
import com.example.containertracker.utils.pickimage.PickImageUtils
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageAdapter
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ContainerDefectFormBottomSheet : BaseBottomSheet() {

    private lateinit var binding: BottomSheetContainerDefectFormBinding
    private val viewModel: ContainerDefectFormViewModel by viewModel()

    private val genericImageAdapter by lazy {
        GenericSelectImageAdapter {
            viewModel.onItemImageListClick(item = it)
        }
    }

    private val imagePreviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.onImagePreviewResult(resultCode = it.resultCode, intent = it.data) }

    // callback to submit
    var onNext: (ContainerDefectParam) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_container_defect_form,
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
        viewModel.setImageContainer()
    }

    private fun mainView() {
        this.binding.viewModelBinding = viewModel.also {
            it.openMediaState.observe(viewLifecycleOwner) { imageModel ->
                showMediaDialog(data = imageModel)
            }
            it.openImagePreviewState.observe(viewLifecycleOwner) { imageModel ->
                showImagePreview(data = imageModel)
            }
            it.imageList.observe(viewLifecycleOwner) {list ->
                handleImageList(list)
            }
            it.loadingState.observe(viewLifecycleOwner) {isLoading ->
                handleLoadingWidget(isLoading)
            }
        }
        with(binding) {

            buttonSubmit.setOnClickListener {
                onNext(viewModel.createContainerDefectParam())
            }

            // setup recycler view
            rvContainerImages.adapter = genericImageAdapter
            rvContainerImages.layoutManager = GridLayoutManager(
                requireContext(),
                4
            )
        }
    }

    private fun handleImageList(list: List<GenericSelectImageUiModel>?) {
        binding.rvContainerImages.post {
            genericImageAdapter.submitList(list)
        }
        binding.buttonSubmit.isEnabled = (list?.filterNot {
            it.imageFilePath.isNullOrBlank()
        }?.size ?: 0) >= 1
    }

    /**
     * show media bottom sheet
     */
    private fun showMediaDialog(data: GenericSelectImageUiModel) {
        showImmediately(childFragmentManager, "show_media") {
            SelectImageBottomSheet.newInstance().apply {
                onImageSelected = {
                    this@ContainerDefectFormBottomSheet.compressImage(data, PickImageUtils.uri)
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
                    resolution(640, 480)
                    quality(75)
                    format(Bitmap.CompressFormat.PNG)
//                    quality(80)
//                    size(1_048_576) // 1 MB
                }
            }
            viewModel.hideLoadingWidget()
            compressedImageFile?.let {
                viewModel.onImageSelected(
                    position = data.position,
                    file = it
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
        ScanFlexiFragment.isProgressInput = false
        val isSuccessSave = result?.getBooleanExtra(ExtrasConstant.EXTRA_SUCCESS_SAVE, false)
        if (isSuccessSave.orFalse()) dismiss()
    }

    companion object {
        fun newInstance(): ContainerDefectFormBottomSheet {
            val sheet = ContainerDefectFormBottomSheet()

            return sheet
        }
    }

}