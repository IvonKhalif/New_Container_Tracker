package com.example.containertracker.utils.pickimage

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.containertracker.R
import java.io.File
import java.io.IOException

@Suppress("unused", "MemberVisibilityCanBePrivate")
object PickImageUtils {
    const val PICK_IMAGE_CHOOSER_REQUEST_CODE = 200

    var uri: Uri? = null

    fun startPickImageActivity(context: Context, fragment: Fragment) {
        fragment.startActivityForResult(
            getPickImageChooserIntent(context), PICK_IMAGE_CHOOSER_REQUEST_CODE
        )
    }

    fun getPickImageChooserIntent(context: Context): Intent {
        return getPickImageChooserIntent(
            context = context,
            title = context.getString(R.string.pick_image_intent_chooser_title),
            includeDocuments = false,
            includeCamera = true
        )
    }

    fun getPickImageChooserIntent(
        context: Context,
        title: CharSequence?,
        includeDocuments: Boolean,
        includeCamera: Boolean
    ): Intent {
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager = context.packageManager
        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager))
        }
//        allIntents.addAll(
//            getGalleryIntents(
//                packageManager,
//                Intent.ACTION_GET_CONTENT,
//                includeDocuments
//            )
//        )
        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(allIntents.removeAt(allIntents.size - 1), title)
        // Add all other intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
        )
        return chooserIntent
    }

    fun getCameraIntents(
        context: Context,
        packageManager: PackageManager
    ): List<Intent> {
        val allIntents: MutableList<Intent> = ArrayList()
        // Determine Uri of camera image to  save.
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val outputFileUri = getCaptureImageOutputUriContent(context)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            allIntents.add(intent)
        }
        // Just in case queryIntentActivities returns emptyList
        if (allIntents.isEmpty()) allIntents.add(captureIntent)
        return allIntents
    }

    fun isExplicitCameraPermissionRequired(context: Context): Boolean = (
            CommonVersionCheck.isAtLeastM23() &&
                    hasPermissionInManifest(context, "android.permission.CAMERA") &&
                    (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            )

    fun hasPermissionInManifest(
        context: Context,
        permissionName: String
    ): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermissions = packageInfo.requestedPermissions
            if (declaredPermissions != null && declaredPermissions.isNotEmpty()) {
                for (p in declaredPermissions) {
                    if (p.equals(permissionName, ignoreCase = true)) return true
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            //To Implement Later
        }
        return false
    }

    fun getCaptureImageOutputUriContent(context: Context): Uri {
//        val fileName = "pickImageResult.jpeg"
        val outputFileUri: Uri
        val getImage = createImageFile(context)
        if (!getImage.exists()){
            try {
                getImage.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        // We have this because of a HUAWEI path bug when we use getUriForFile
        outputFileUri = if (CommonVersionCheck.isAtLeastQ29()) {
    //            getImage = context.cacheDir
            try {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + CommonValues.authority,
                    getImage
                )
            } catch (e: Exception) {
                Uri.fromFile(getImage)
            }
        } else {
    //            getImage = context.cacheDir
            Uri.fromFile(getImage)
        }
        Log.e("TAG", "getCaptureImageOutputUriContent:$outputFileUri ", )
        return outputFileUri
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val storageDir: File? = context.cacheDir
        return File.createTempFile(
            "IMG_${System.currentTimeMillis()}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        )
    }

    fun getPickImageResultUriContent(context: Context, data: Intent?): Uri {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera || data!!.data == null) getCaptureImageOutputUriContent(context)
        else data.data!!
    }
}