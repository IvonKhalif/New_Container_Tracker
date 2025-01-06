package com.example.containertracker.ui.seal.preview

import android.util.Base64
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.seal.requests.SaveSealRequest
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.localsales.usecase.SaveSealLocalSalesUseCase
import com.example.containertracker.domain.seal.usecase.SaveSealUseCase
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

class SealPreviewViewModel(
    private val saveSealUseCase: SaveSealUseCase,
    private val saveSealLocalSalesUseCase: SaveSealLocalSalesUseCase
): BaseViewModel() {

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun onSubmit(containerData: Container?, imageListData: ArrayList<GenericSelectImageUiModel>?) = viewModelScope.launch {
        val isLocalSales = UserUtil.getDepartmentId() == RoleAccessEnum.LOCALSALES.value
        val requestParam = withContext(Dispatchers.IO) {
            createSaveModel(containerData, imageListData)
        }
        if (isLocalSales) saveSealLocalSales(requestParam) else saveSealRegular(requestParam)
    }

    private fun createSaveModel(
        container: Container?,
        imageListData: ArrayList<GenericSelectImageUiModel>?
    ): SaveSealRequest {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)

        return SaveSealRequest(
            userId = user?.id,
            salesOrderDetailId = container?.salesOrderDetailId,
            typeContainer= container?.typeContainer,
            photo1 = getBase64(imageListData, "1"),
            photo2 = getBase64(imageListData, "2"),
            photo3 = getBase64(imageListData, "3"),
            photo4 = getBase64(imageListData, "4"),
            photo5 = getBase64(imageListData, "5"),
            photo6 = getBase64(imageListData, "6"),
            photo7 = getBase64(imageListData, "7"),
            photo8 = getBase64(imageListData, "8")
        )
    }


    private fun getBase64(pictures: ArrayList<GenericSelectImageUiModel>?, position: String): String? {
        val filePath = pictures.orEmpty().firstOrNull { it.position == position }?.imageFilePath

        if (filePath.isNullOrBlank()) return null

        val file = File(filePath)

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }

    private fun saveSealRegular(requestParam: SaveSealRequest) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = saveSealUseCase(requestParam)) {
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

    private fun saveSealLocalSales(requestParam: SaveSealRequest) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = saveSealLocalSalesUseCase(requestParam)) {
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