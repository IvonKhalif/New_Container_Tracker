package com.example.containertracker.ui.flexi.preview

import android.util.Base64
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.flexi.requests.SaveFlexiRequest
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.flexi.usecase.SaveFlexiUseCase
import com.example.containertracker.domain.localsales.usecase.SaveFlexiLocalSalesUseCase
import com.example.containertracker.ui.flexi.form.FlexiFormParam
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.UserUtil
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FlexiPreviewViewModel(
    private val saveFlexiUseCase: SaveFlexiUseCase,
    private val saveFlexiLocalSalesUseCase: SaveFlexiLocalSalesUseCase
) : BaseViewModel() {

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun onSubmit(flexiParam: FlexiFormParam) = viewModelScope.launch {
        val isLocalSales = UserUtil.getDepartmentId() == RoleAccessEnum.LOCALSALES.value
        val requestParam = withContext(Dispatchers.IO) {
            createSaveModel(flexiParam)
        }
        if (isLocalSales) saveFlexiLocalSales(requestParam) else saveFlexiRegular(requestParam)
    }

    private fun createSaveModel(param: FlexiFormParam): SaveFlexiRequest {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)

        return SaveFlexiRequest(
            userId = user?.id,
            salesOrderDetailId = param.containerData.salesOrderDetailId,
            flexiCap = param.flexiCap,
            flexiNumber = param.flexiNumber,
            photo1 = getBase64(param.containerPictures, "1"),
            photo2 = getBase64(param.containerPictures, "2"),
            photo3 = getBase64(param.containerPictures, "3"),
            photo4 = getBase64(param.containerPictures, "4"),
            photo5 = getBase64(param.containerPictures, "5"),
            photo6 = getBase64(param.containerPictures, "6"),
            photo7 = getBase64(param.containerPictures, "7"),
            photo8 = getBase64(param.containerPictures, "8"),
            photo9 = getBase64(param.containerPictures, "9"),
            photo10 = getBase64(param.containerPictures, "10"),
            photo11 = getBase64(param.containerPictures, "11"),
            photo12 = getBase64(param.containerPictures, "12"),
        )
    }


    private fun getBase64(pictures: List<GenericSelectImageUiModel>, position: String): String? {
        val filePath = pictures.firstOrNull { it.position == position }?.imageFilePath

        if (filePath.isNullOrBlank()) return null

        val file = File(filePath)

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }

    private fun saveFlexiRegular(requestParam: SaveFlexiRequest) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = saveFlexiUseCase(requestParam)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onSuccessSubmit.post()
                } else {
                    _serverErrorState.value = GenericErrorResponse(
                        status = response.body.status,
                        message = response.body.toString()
                    )
                }
            }

            is NetworkResponse.ServerError -> {
                _serverErrorState.value = response.body
            }

            is NetworkResponse.NetworkError -> {
                _networkErrorState.value = response.error
            }
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
        hideLoadingWidget()
    }

    private fun saveFlexiLocalSales(requestParam: SaveFlexiRequest) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = saveFlexiLocalSalesUseCase(requestParam)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onSuccessSubmit.post()
                } else {
                    _serverErrorState.value = GenericErrorResponse(
                        status = response.body.status,
                        message = response.body.toString()
                    )
                }
            }
            is NetworkResponse.ServerError -> {
                _serverErrorState.value = response.body
            }
            is NetworkResponse.NetworkError -> {
                _networkErrorState.value = response.error
            }
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
        hideLoadingWidget()
    }
}