package com.example.containertracker.ui.tally.formpicture

import android.app.Activity
import android.content.Intent
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.domain.tally.usecase.SavePhotoUseCase
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class TallyFormPictureViewModel(
    private val savePhotoUseCase: SavePhotoUseCase
): BaseViewModel() {
    private val _imageList = MutableLiveData<List<GenericSelectImageUiModel>>()
    val imageList: LiveData<List<GenericSelectImageUiModel>> get() = _imageList
    private val _openMediaState = MutableLiveData<GenericSelectImageUiModel>()
    val openMediaState: LiveData<GenericSelectImageUiModel> get() = _openMediaState
    private val _openImagePreviewState = MutableLiveData<GenericSelectImageUiModel>()
    val openImagePreviewState: LiveData<GenericSelectImageUiModel> get() = _openImagePreviewState

    private val _tallySheetData = MutableLiveData<TallySheetDetailResponse>()
    val tallySheetData: LiveData<TallySheetDetailResponse> get() = _tallySheetData

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun initDataTally(data: TallySheetDetailResponse) {
        _tallySheetData.value = data
    }

    fun setImageContainer() {
        val images = mutableListOf<GenericSelectImageUiModel>()
        val listOfPosition = arrayListOf<String>()

        for (i in 1..30) listOfPosition.add(i.toString())

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
        savePhoto(position)
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

    private fun savePhoto(position: String) = CoroutineScope(Dispatchers.IO).launch {
        when(val response = savePhotoUseCase(
            tallySheetId = tallySheetData.value?.idTallySheet.orEmpty(),
            itemId = position,
            photo = getBase64(position).orEmpty()
        )) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    println("Success save photo Tally")
                } else {
                    System.err.println("This is an error message. ${response.body}")
                }
            }
            is NetworkResponse.ServerError ->  System.err.println("This is an error message. ${response.body}")
            is NetworkResponse.NetworkError -> _networkErrorState.value = response.error
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
    }

    private fun getBase64(position: String): String? {
        val filePath = _imageList.value?.firstOrNull { it.position == position }?.imageFilePath

        if (filePath.isNullOrBlank()) return null

        val file = File(filePath)

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }

    fun renderPhotoFromServer(tallySheetDetailResponse: TallySheetDetailResponse) {
        val images = _imageList.value.orEmpty().toMutableList()
        val photo = tallySheetDetailResponse.photo
        if (!photo?.photo1.isNullOrBlank()) images[0] = images[0].copy(imageFilePath = photo?.photo1)
        if (!photo?.photo2.isNullOrBlank()) images[1] = images[1].copy(imageFilePath = photo?.photo2)
        if (!photo?.photo3.isNullOrBlank()) images[2] = images[2].copy(imageFilePath = photo?.photo3)
        if (!photo?.photo4.isNullOrBlank()) images[3] = images[3].copy(imageFilePath = photo?.photo4)
        if (!photo?.photo5.isNullOrBlank()) images[4] = images[4].copy(imageFilePath = photo?.photo5)
        if (!photo?.photo6.isNullOrBlank()) images[5] = images[5].copy(imageFilePath = photo?.photo6)
        if (!photo?.photo7.isNullOrBlank()) images[6] = images[6].copy(imageFilePath = photo?.photo7)
        if (!photo?.photo8.isNullOrBlank()) images[7] = images[7].copy(imageFilePath = photo?.photo8)
        if (!photo?.photo9.isNullOrBlank()) images[8] = images[8].copy(imageFilePath = photo?.photo9)
        if (!photo?.photo10.isNullOrBlank()) images[9] = images[9].copy(imageFilePath = photo?.photo10)
        if (!photo?.photo11.isNullOrBlank()) images[10] = images[10].copy(imageFilePath = photo?.photo11)
        if (!photo?.photo12.isNullOrBlank()) images[11] = images[11].copy(imageFilePath = photo?.photo12)
        if (!photo?.photo13.isNullOrBlank()) images[12] = images[12].copy(imageFilePath = photo?.photo13)
        if (!photo?.photo14.isNullOrBlank()) images[13] = images[13].copy(imageFilePath = photo?.photo14)
        if (!photo?.photo15.isNullOrBlank()) images[14] = images[14].copy(imageFilePath = photo?.photo15)
        if (!photo?.photo16.isNullOrBlank()) images[15] = images[15].copy(imageFilePath = photo?.photo16)
        if (!photo?.photo17.isNullOrBlank()) images[16] = images[16].copy(imageFilePath = photo?.photo17)
        if (!photo?.photo18.isNullOrBlank()) images[17] = images[17].copy(imageFilePath = photo?.photo18)
        if (!photo?.photo19.isNullOrBlank()) images[18] = images[18].copy(imageFilePath = photo?.photo19)
        if (!photo?.photo20.isNullOrBlank()) images[19] = images[19].copy(imageFilePath = photo?.photo20)
        if (!photo?.photo21.isNullOrBlank()) images[20] = images[20].copy(imageFilePath = photo?.photo21)
        if (!photo?.photo22.isNullOrBlank()) images[21] = images[21].copy(imageFilePath = photo?.photo22)
        if (!photo?.photo23.isNullOrBlank()) images[22] = images[22].copy(imageFilePath = photo?.photo23)
        if (!photo?.photo24.isNullOrBlank()) images[23] = images[23].copy(imageFilePath = photo?.photo24)
        if (!photo?.photo25.isNullOrBlank()) images[24] = images[24].copy(imageFilePath = photo?.photo25)
        if (!photo?.photo26.isNullOrBlank()) images[25] = images[25].copy(imageFilePath = photo?.photo26)
        if (!photo?.photo27.isNullOrBlank()) images[26] = images[26].copy(imageFilePath = photo?.photo27)
        if (!photo?.photo28.isNullOrBlank()) images[27] = images[27].copy(imageFilePath = photo?.photo28)
        if (!photo?.photo29.isNullOrBlank()) images[28] = images[28].copy(imageFilePath = photo?.photo29)
        if (!photo?.photo30.isNullOrBlank()) images[29] = images[29].copy(imageFilePath = photo?.photo30)

        _imageList.value = images
    }
}