package com.example.containertracker.ui.flexi.preview

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.ActivityFlexiPreviewBinding
import com.example.containertracker.ui.flexi.form.FlexiFormParam
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageAdapter
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FlexiPreviewActivity : BaseActivity() {

    private lateinit var binding: ActivityFlexiPreviewBinding
    private val viewModel: FlexiPreviewViewModel by viewModel()

    private val flexiData by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_FLEXI_DATA, FlexiFormParam::class.java)
        } else {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_FLEXI_DATA)
        }
    }

    private val genericImageAdapter by lazy {
        GenericSelectImageAdapter {
//            viewModel.onItemImageListClick(item = it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlexiPreviewBinding.inflate(layoutInflater)
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
            flexiData?.let { data ->
                textFlexiCap.text = data.flexiCap
                textFlexiNumber.text = data.flexiNumber
                textProductName.text = data.containerData.codeContainer
                updateImageList(data.containerPictures)
            }
            // setup recycler view
            rvContainerImages.adapter = genericImageAdapter
            rvContainerImages.layoutManager = GridLayoutManager(
                this@FlexiPreviewActivity,
                4
            )

            buttonSubmit.setOnClickListener {
                flexiData?.let { flexi -> viewModel.onSubmit(flexi) }
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