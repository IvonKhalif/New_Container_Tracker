package com.example.containertracker.ui.media.select_image

import android.R.attr.bitmap
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
//    private val cameraLauncher =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) {
//            Log.e("TAG", "cameraLauncher: $it")
////            Log.e("TAG", "cameraLauncher: ${it.resultCode} ${it.data?.data}", )
////            if (it.resultCode == AppCompatActivity.RESULT_OK) {
////                onImageSelected(it.data?.data)
////            }
//            if (it) onImageSelected(uri)
//        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success -> // Gunakan nama yang lebih deskriptif
            Log.e("TAG", "cameraLauncher: $success") // Log hasilnya dengan jelas
            if (success) {
                // Jika pengambilan gambar berhasil, URI yang diberikan sebelumnya seharusnya berisi gambar yang diambil.
                uri?.let { imageUri ->  //gunakan uri yang sudah ada
                    moveImageToPermanentLocation(imageUri)
                } ?: run {
                    // Handle kasus di mana uri adalah null (ini seharusnya tidak terjadi, tetapi lebih baik ditangani)
                    Log.e("TAG", "URI is null after successful picture capture")
                    // Anda mungkin ingin menampilkan pesan kesalahan kepada pengguna di sini.
                }
            } else {
                Log.e("TAG", "Failed to capture image")
            }
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
        // Membuat file sementara untuk menyimpan gambar yang diambil oleh kamera
        val imageFile = createImageFileTemp(context)

        // Memastikan file berhasil dibuat
        imageFile?.let { file ->
            // Mendapatkan URI untuk file menggunakan FileProvider
            val imageUri: Uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + CommonValues.authority,
                file
            )
            uri = imageUri  // Simpan URI sementara

            Log.e("TAG", "chooseImage (Temporary content://): $imageUri")

            // Memulai intent untuk mengambil gambar
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    // Menyediakan URI sementara sebagai lokasi penyimpanan
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    cameraLauncher.launch(imageUri)
                }
            }
        } ?: run {
            Log.e("TAG", "Failed to create image file")
            // Handle error
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
                chooseImage()
            }
        }
    }

    fun onPagePermissionsDenied() {
        showPermissionRationaleModal(permissions)
    }

    // Fungsi untuk membuat file sementara
    private fun createImageFileTemp(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "TEMP_IMG_${timeStamp}.png" // Prefix "TEMP_" untuk file sementara
        val storageDir = context.cacheDir
        return try {
            File.createTempFile(
                imageFileName,
                ".png",
                storageDir
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun moveImageToPermanentLocation(tempUri: Uri) = useContext { context ->
        val contentResolver = context.contentResolver
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageName = "IMG_${timeStamp}.png"  // Nama file permanen
        val permanentDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "Container Tracker"
        )

        // Membuat direktori jika belum ada
        if (!permanentDir.exists()) {
            if (!permanentDir.mkdirs()) {
                Log.e("TAG", "Failed to create directory: ${permanentDir.absolutePath}")
                // Handle kegagalan membuat direktori
                return@useContext
            }
        }

        val permanentFile = File(permanentDir, imageName)
        val permanentUri = Uri.fromFile(permanentFile) // Dapatkan Uri untuk file permanen

        try {
            // Salin data dari Uri sementara ke Uri permanen menggunakan ContentResolver
            val inputStream = contentResolver.openInputStream(tempUri) ?: throw IOException("Failed to open input stream")
            val outputStream = contentResolver.openOutputStream(permanentUri) ?: throw IOException("Failed to open output stream")
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            // Hapus file sementara setelah berhasil disalin
            contentResolver.delete(tempUri, null, null)
            Log.e("TAG", "Image moved to: ${permanentUri.path}")
            // Sekarang Anda memiliki URI gambar permanen (permanentUri)
            onImageSelected(permanentUri) // Beri tahu UI bahwa gambar sudah siap.
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("TAG", "Error moving image: ${e.message}")
            // Handle kesalahan penyalinan
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