package com.example.containertracker.ui.seal.preview

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.ActivitySealPreviewBinding
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageAdapter
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SealPreviewActivity : BaseActivity() {
    private lateinit var binding: ActivitySealPreviewBinding
    private val viewModel: SealPreviewViewModel by viewModel()

    private val imageListData by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(ExtrasConstant.EXTRA_IMAGE_LIST_DATA, GenericSelectImageUiModel::class.java)
        } else {
            intent.getParcelableArrayListExtra<GenericSelectImageUiModel>(ExtrasConstant.EXTRA_IMAGE_LIST_DATA)
        }
    }

    private val containerData by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_CONTAINER_DATA, Container::class.java)
        } else {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_CONTAINER_DATA)
        }
    }

    private val isLocalSales by lazy {
        intent.getBooleanExtra(ExtrasConstant.EXTRA_IS_LOCAL_SALES, false)
    }

    private val genericImageAdapter by lazy {
        GenericSelectImageAdapter {
//            viewModel.onItemImageListClick(item = it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySealPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainView()
        initObserver()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun mainView() {
        supportActionBar?.title = getString(R.string.flexi_preview_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            imageListData?.let { data ->
                updateImageList(data)
            }
            containerData?.let { data ->
                textProductName.text = data.codeContainer
                textDgStatus.text = data.dgClass
                rvContainerImages.adapter = genericImageAdapter
                rvContainerImages.layoutManager = GridLayoutManager(
                    this@SealPreviewActivity,
                    if (data.maxPhoto == 8) 4 else 2
                )
            }

            buttonSubmit.setOnClickListener {
                viewModel.onSubmit(containerData, imageListData, isLocalSales)
            }
        }
    }

    private fun initObserver() {
        this.binding.viewModelBinding = viewModel.also {
            it.loadingState.observe(this) { isLoading ->
                handleLoadingWidget(isLoading)
            }
            it.networkErrorState.observe(this) { error ->
                showErrorMessage(error)
            }
            it.serverErrorState.observe(this) { error ->
                showErrorMessage(error.status.orEmpty())
            }
            it.onSuccessSubmit.observeAction(this) {
                handleSuccess()
            }
        }
    }

    private fun updateImageList(list: List<GenericSelectImageUiModel>) {
        binding.rvContainerImages.post {
            genericImageAdapter.submitList(list)
        }
    }

    private fun handleSuccess() {
        val intent = Intent()
        intent.putExtra(ExtrasConstant.EXTRA_SUCCESS_SAVE, true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}