package com.example.containertracker.ui.media.image_preview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.ablanco.zoomy.Zoomy
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.containertracker.R
import com.example.containertracker.base.useContext
import com.example.containertracker.databinding.ActivityImagePreviewBinding
import com.example.containertracker.ui.media.select_image.SelectImageBottomSheet
import com.example.containertracker.utils.extension.setFullScreen
import com.example.containertracker.utils.media.FileUtil
import com.example.containertracker.utils.pickimage.PickImageUtils
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagePreviewActivity : AppCompatActivity() {

    private var _binding: ActivityImagePreviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ImagePreviewViewModel>()

    private val intentImagePath by lazy {
        intent.getStringExtra(ARG_FILE_PATH)
    }

    private val intentConfig by lazy {
        intent.getParcelableExtra<ImagePreviewConfig>(ARG_CONFIG)
    }

    private val circularProgressDrawable by lazy {
        CircularProgressDrawable(binding.root.context).also {
            it.strokeWidth = 5f
            it.centerRadius = 30f
            it.setColorSchemeColors(R.color.lightGreen)
            it.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // remove title
        super.onCreate(savedInstanceState)

        _binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initObserver()
    }

    override fun onResume() {
        setFullScreen()
        super.onResume()
    }

    private fun initView() = with(binding) {
        viewModel.onImagePathChanged(intentImagePath)

        btnDone.isVisible = intentConfig?.buttonDoneEnable ?: true
        btnClose.isVisible = intentConfig?.buttonBackEnable ?: true
        btnEdit.isVisible = intentConfig?.buttonEditEnable ?: true
        btnRemove.isVisible = intentConfig?.buttonRemoveEnable ?: true

        val builder = Zoomy.Builder(this@ImagePreviewActivity).target(binding.ivContainerImage)
        builder.register()
    }

    private fun initEvent() = with(binding) {
        btnClose.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            showMediaDialog()
        }

        btnRemove.setOnClickListener {
            viewModel.onRemove()
        }

        btnDone.setOnClickListener {
            viewModel.onDone()
        }

        ivContainerImage.setOnClickListener {
            viewModel.onContainerImageClick()
        }
    }

    /**
     * observe live data from viewmodel
     */
    private fun initObserver() = with(viewModel) {
        imagePath.observe(this@ImagePreviewActivity) {
            Glide.with(this@ImagePreviewActivity)
                .load(it)
                .error(R.drawable.ic_baseline_sync_24)
                .placeholder(circularProgressDrawable)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewModel.onLoadImageResult(isError = true)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewModel.onLoadImageResult(isError = false)
                        return false
                    }
                })
                .into(binding.ivContainerImage)
        }

        resultState.observe(this@ImagePreviewActivity) {
            finishWithResult(it)
        }
    }

    /**
     * show media bottom sheet
     */
    private fun showMediaDialog() {
        val media = SelectImageBottomSheet.newInstance()

        media.onImageSelected = {
            compressImage(PickImageUtils.uri)
            media.dismissAllowingStateLoss()
        }

        media.showNow(supportFragmentManager, media.javaClass.canonicalName)
    }

    private fun compressImage(uri: Uri?) {
        val file = uri?.let { FileUtil.from(this, it) }
        CoroutineScope(Dispatchers.Main).launch {
            val compressedImageFile = file?.let {
                Compressor.compress(this@ImagePreviewActivity, it) {
                    resolution(1280, 720)
                    quality(80)
                    size(1_048_576) // 1 MB
                }
            }
            compressedImageFile?.let {
                viewModel.onImagePathChanged(it.absolutePath)
            }
        }
    }

    /**
     * set activity for result with parcel
     */
    private fun finishWithResult(filePath: String?) {
        val parcel = Intent().apply {
            putExtra(ARG_FILE_PATH, filePath)
        }
        setResult(Activity.RESULT_OK, parcel)
        finish()
    }

    override fun onDestroy() {
        Zoomy.unregister(binding.ivContainerImage)
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARG_FILE_PATH = "file_path"
        const val ARG_CONFIG = "config"

        fun open(
            context: Context,
            launcher: ActivityResultLauncher<Intent>,
            filePath: String,
            config: ImagePreviewConfig = ImagePreviewConfig()
        ) {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(ARG_FILE_PATH, filePath)
            intent.putExtra(ARG_CONFIG, config)
            launcher.launch(intent)
        }
    }
}