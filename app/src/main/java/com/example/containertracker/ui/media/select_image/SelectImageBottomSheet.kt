package com.example.containertracker.ui.media.select_image

import android.R.attr.bitmap
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.containertracker.BuildConfig
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.base.runPermissionRequest
import com.example.containertracker.base.showPermissionRationaleModal
import com.example.containertracker.base.useContext
import com.example.containertracker.utils.enums.PermissionEnum
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.media.CameraHelper
import com.example.containertracker.utils.media.FileUtil
import com.example.containertracker.utils.media.GalleryHelper
import com.example.containertracker.utils.pickimage.CommonValues
import com.example.containertracker.utils.pickimage.PickImageUtils
import com.example.containertracker.utils.pickimage.PickImageUtils.uri
import com.example.containertracker.widget.menu_bottom_sheet.MenuIconWithHeadlineAdapter
import com.example.containertracker.widget.menu_bottom_sheet.MenuIconWithHeadlineBottomSheet
import com.example.containertracker.widget.select_image.SelectImageMenuType
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


/**
 * Created by yovi.putra on 20/07/22"
 * Project name: Container Tracker
 **/


class SelectImageBottomSheet(
    private val fileImageOnSelected: ((File) -> Unit)? = null
) : MenuIconWithHeadlineBottomSheet<SelectImageMenuType>() {

    private val saveImageToDirectory by lazy {
        arguments?.getBoolean(SAVE_IMAGE_TO_DIRECTORY).orFalse()
    }

    private var permissions = mutableListOf<PermissionEnum>()

    // Camera helper
    private val cameraHelper by lazy { CameraHelper(requireContext()) }

    // Camera Launcher
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.e("TAG", "cameraLauncher: $it")
//            Log.e("TAG", "cameraLauncher: ${it.resultCode} ${it.data?.data}", )
//            if (it.resultCode == AppCompatActivity.RESULT_OK) {
//                onImageSelected(it.data?.data)
//            }
            if (it) onImageSelected(uri)
        }

    // Camera Permission Launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions -> cameraHelper.onPermissionForResult(permissions = permissions) }

    // Gallery helper
    private val galleryHelper by lazy { GalleryHelper() }

    // Gallery Launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri)
    }

    // Gallery Permission Launcher
    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions -> galleryHelper.onPermissionForResult(permissions = permissions) }

    var onImageSelected: (File?) -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()
    }

    private fun initView() {
        binding.tvTitle.text = getString(R.string.choose_media)

        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.run {
                    val granted = filter { p -> p.value }
                    when (granted.size) {
                        this.size -> onPagePermissionsGranted()
                        else -> onPagePermissionsDenied()
                    }
                }
            }

        adapter = MenuIconWithHeadlineAdapter<SelectImageMenuType>(
            setHeadline = { getString(it.title) },
            setIconLeft = { it.icon },
            areItemsTheSame = { old, new -> old == new },
            areContentTheSame = { oldItem, newItem -> oldItem.title == newItem.title },
            onClick = {
                when (it) {
                    is SelectImageMenuType.Gallery -> galleryHelper.open(launcher = galleryLauncher)
                    is SelectImageMenuType.Camera -> {
                        permissions = mutableListOf(PermissionEnum.CAMERA)
                        val permissionsString = permissions.map { it.permission }
                        runPermissionRequest(permissionsString.toTypedArray(), onGranted = {
                            chooseImage()
                        }, onRational = {
                            showPermissionRationaleModal(permissions)
                        })
//                        activity?.let { activity ->
//                            val intent = Intent(activity, CameraActivity::class.java)
//                            cameraLauncher.launch(intent)
//                        }
                    }
                }
            }
        ).also {
            it.submitList(
                listOf(
                    SelectImageMenuType.Camera,
                    SelectImageMenuType.Gallery
                )
            )
        }

        binding.menuList.adapter = adapter
    }

    private fun initEvent() {
        cameraHelper.onNeedPermission = {
            cameraPermissionLauncher.launch(it)
        }

        galleryHelper.onNeedPermission = {
            galleryPermissionLauncher.launch(it)
        }
    }

    private fun chooseImage() = useContext { context ->
        val file = if (saveImageToDirectory)
            File(createImageUri()?.path ?: "")
        else
            PickImageUtils.createImageFileTemp(context)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
//        PickImageUtils.startPickImageActivity(it, this)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Continue only if the File was successfully created
                file.also { f ->
                    uri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + CommonValues.authority,
                        f
                    )
                    Log.e("TAG", "chooseImage: $uri")
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    cameraLauncher.launch(uri)
                }
            }
        }
    }

    private fun compressImage(uri: Uri?) = useContext { usableContext ->
        val file = uri?.let { FileUtil.from(requireContext(), it) }
        CoroutineScope(Dispatchers.Main).launch {
            val compressedImageFile = file?.let {
                Compressor.compress(usableContext, it) {
                    resolution(1280, 720)
                    quality(80)
                    size(1_048_576) // 1 MB
                }
            }
            compressedImageFile?.let { onImageSelected.invoke(it) }
        }
    }

    private fun onImageSelected(uri: Uri?) = useContext { usableContext ->
        if (uri == null) return@useContext
//        startActivity(Intent(usableContext, MainActivity::class.java))
        PickImageUtils.uri = uri
        val file = File(uri.path ?: "")
        if (!file.exists()) {
            try {
                Log.e("Camera Result", "onActivityResult: ${PickImageUtils.uri?.path}")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        MediaScannerConnection.scanFile(
            context,
            arrayOf(uri.path),
            arrayOf("image/png"),
            null
        )
        onImageSelected.invoke(null)
        dismiss()
        /*showLoading(true)

        CoroutineScope(Dispatchers.Main).launch {
//            runCatching {
                val file = FileUtil.from(usableContext, uri)
                val result = Compressor.compress(
                    context = usableContext,
                    imageFile = file
                ) {
                    resolution(1280, 720)
                    quality(80)
                    size(1_048_576) // 1 MB
                }

                // result can bigger from original
                val resultSelectedImage = if (file.length() < result.length()) {
                    file
                } else {
                    result
                }

            this@SelectImageBottomSheet.showLoading(false)
//            }.onSuccess {
//                showLoading(false)
            fileImageOnSelected?.invoke(resultSelectedImage)
//                onImageSelected.invoke(resultSelectedImage)

//            }.onFailure {
//                showLoading(false)
//            }
        }*/
    }

    private fun showLoading(show: Boolean) {
        (requireActivity() as? BaseActivity)?.handleLoadingWidget(childFragmentManager, show)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
        useContext { usableContext ->
            super.onActivityResult(requestCode, resultCode, data)
            Log.e("TAG", "onActivityResult: $requestCode $resultCode ${data?.data}")
            if ((requestCode == PickImageUtils.PICK_IMAGE_CHOOSER_REQUEST_CODE) &&
                resultCode == Activity.RESULT_OK
            ) {
                uri = PickImageUtils.getPickImageResultUriContent(usableContext, data)
                Log.e("Camera Result", "onActivityResult: ${uri?.path}")
                if (FileUtil.isImageFileType(uri, usableContext)) {
                    compressImage(uri)
                } else {
                    showErrorMessage(getString(R.string.general_error_message))
                }
                dismissAllowingStateLoss()
            }
        }

    fun onPagePermissionsGranted() {
        when {
            permissions.contains(PermissionEnum.CAMERA) -> {
//                showChangePhotoBottomSheet()
                chooseImage()
            }
        }
    }

    fun onPagePermissionsDenied() {
        showPermissionRationaleModal(permissions)
    }

    private fun createImageUri(): Uri? {
        return context?.let { ctx ->
            val resolver = ctx.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + "/Container Tracker/"
                ) // Create a subfolder within DCIM    }
            }
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
    }

    companion object {
        private const val SAVE_IMAGE_TO_DIRECTORY = "SAVE_IMAGE_TO_DIRECTORY"

        fun newInstance(
            saveImageToDirectory: Boolean = false
        ) = SelectImageBottomSheet().apply {
            arguments = Bundle().apply {
                putBoolean(SAVE_IMAGE_TO_DIRECTORY, saveImageToDirectory)
            }
        }
    }
}