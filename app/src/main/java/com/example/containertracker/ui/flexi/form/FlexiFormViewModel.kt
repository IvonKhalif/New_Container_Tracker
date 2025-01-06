package com.example.containertracker.ui.flexi.form

import android.app.Activity
import android.content.Intent
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.utils.constants.ContainerConstant
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import java.io.File

class FlexiFormViewModel : BaseViewModel() {
    var flexiCap = MutableLiveData("")
    var flexiNumber = MutableLiveData("")

    private val _imageList = MutableLiveData<List<GenericSelectImageUiModel>>()
    val imageList: LiveData<List<GenericSelectImageUiModel>> get() = _imageList
    private val _openMediaState = MutableLiveData<GenericSelectImageUiModel>()
    val openMediaState: LiveData<GenericSelectImageUiModel> get() = _openMediaState
    private val _openImagePreviewState = MutableLiveData<GenericSelectImageUiModel>()
    val openImagePreviewState: LiveData<GenericSelectImageUiModel> get() = _openImagePreviewState

    fun setImageContainer() {
        val images = mutableListOf<GenericSelectImageUiModel>()
        val listOfPosition = arrayListOf<String>()

        for (i in 1..12) listOfPosition.add(i.toString())

        listOfPosition.forEach {
            images.add(GenericSelectImageUiModel(position = it))
        }

        _imageList.value = images
    }

    /**
     * handling result from image preview
     */
    fun onImagePreviewResult(resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            val filePath = intent.getStringExtra(ImagePreviewActivity.ARG_FILE_PATH)
            // get position from event previous
            val position = _openImagePreviewState.value?.position
            // prepare assign to imageList
            val images = _imageList.value.orEmpty().toMutableList()

            // update to list with new image state
            images.forEachIndexed { index, uiModel ->
                if (uiModel.position == position) {
                    images[index] = uiModel.copy(imageFilePath = filePath)
                    return@forEachIndexed
                }
            }

            _imageList.value = images
        }
    }

    /**
     * handling image selected from view to update to image list
     */
    fun onImageSelected(position: String, file: File) {
        val images = _imageList.value.orEmpty().toMutableList()

        images.forEachIndexed { index, uiModel ->
            if (uiModel.position == position) {
                images[index] = uiModel.copy(imageFilePath = file.absolutePath)
                return@forEachIndexed
            }
        }

        _imageList.value = images
    }

    /**
     * handling item on click at recycler view item list
     */
    fun onItemImageListClick(item: GenericSelectImageUiModel) {
        if (item.imageFilePath.isNullOrBlank()) {
            _openMediaState.value = item
        } else {
            _openImagePreviewState.value = item
        }
    }

    fun createFlexiParam(container: Container) = FlexiFormParam(
        flexiCap = flexiCap.value.orEmpty(),
        flexiNumber = flexiNumber.value.orEmpty(),
        containerData = container,
        containerPictures = imageList.value.orEmpty(),
    )
}