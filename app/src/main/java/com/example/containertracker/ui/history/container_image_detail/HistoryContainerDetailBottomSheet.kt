package com.example.containertracker.ui.history.container_image_detail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.databinding.BottomSheetHistoryContainerDetailBinding
import com.example.containertracker.domain.history.history_detail.model.HistoryDetail
import com.example.containertracker.ui.history.container_image_detail.adapter.HistoryContainerImageAdapter
import com.example.containertracker.ui.history.container_image_detail.models.HistoryConditionImageUiModel
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.ui.media.image_preview.ImagePreviewConfig
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryContainerDetailBottomSheet : BaseBottomSheet() {

    private var _binding: BottomSheetHistoryContainerDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<HistoryContainerDetailViewModel>()

    private val adapter by lazy {
        HistoryContainerImageAdapter(::onImageClick)
    }

    private val historyTrackingId by lazy { arguments?.getInt(DETAIL_TRACKING_ID) ?: 0 }
    private val conditionImageData by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(CONDITION_IMAGE_DATA, HistoryDetail::class.java)
        } else {
            arguments?.getParcelable(CONDITION_IMAGE_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetHistoryContainerDetailBinding.inflate(
            inflater,
            container,
            false
        )

        initView()
        initEvent()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        viewModel.initPage(id = historyTrackingId, argsConditionImage = conditionImageData)
    }

    private fun initView() = with(binding) {
        rvContainerCondition.adapter = adapter
        rvContainerCondition.layoutManager = GridLayoutManager(
            requireContext(),
            3
        )
    }

    private fun initEvent() = with(binding) {
        btnClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun initObserver() = with(viewModel) {
        loadingState.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        networkErrorState.observe(viewLifecycleOwner) {
            showErrorMessage(it)
            dismissAllowingStateLoss()
        }

        serverErrorState.observe(viewLifecycleOwner) {
            showErrorMessage(it.status.orEmpty())
            dismissAllowingStateLoss()
        }

        conditionImageList.observe(viewLifecycleOwner) {
            binding.rvContainerCondition.post {
                adapter.submitList(it.images)
            }
        }
    }

    private fun onImageClick(data: HistoryConditionImageUiModel) {
        ImagePreviewActivity.open(
            context = requireContext(),
            launcher = resultLauncher,
            filePath = data.imageFullUrl,
            config = ImagePreviewConfig(
                buttonDoneEnable = false,
                buttonEditEnable = false,
                buttonRemoveEnable = false
            )
        )
    }

    companion object {
        private const val DETAIL_TRACKING_ID = "detail_tracking_id"
        private const val CONDITION_IMAGE_DATA = "CONDITION_IMAGE_DATA"

        fun newInstance(
            detailTrackingId: Int,
            conditionImageData: HistoryDetail?
        ) = HistoryContainerDetailBottomSheet().apply {
            arguments = Bundle().apply {
                putInt(DETAIL_TRACKING_ID, detailTrackingId)
                putParcelable(CONDITION_IMAGE_DATA, conditionImageData)
            }
        }
    }
}