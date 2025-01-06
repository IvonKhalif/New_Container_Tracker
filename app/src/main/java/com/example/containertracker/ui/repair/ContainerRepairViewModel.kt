package com.example.containertracker.ui.repair

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.repair.request.SaveContainerRepairRequest
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.repair.SaveContainerRepairUseCase
import com.example.containertracker.domain.repair.ScanContainerRepairUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import java.io.File

class ContainerRepairViewModel(
    private val scanContainerRepairUseCase: ScanContainerRepairUseCase,
    private val saveContainerRepairUseCase: SaveContainerRepairUseCase
): BaseViewModel() {
    var containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()
    val qrCodeLiveData = MutableLiveData<String>()
    val flagScanLiveData = MutableLiveData<FlagScanEnum>()

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun getContainer(qrCode: String = "", flagScan: FlagScanEnum) = viewModelScope.launch {
        qrCodeLiveData.value = qrCode
        flagScanLiveData.value = flagScan
        showLoadingWidget()
        when (val response = scanContainerRepairUseCase(qrCode, containerCode.value, flagScan.type)) {
            is NetworkResponse.Success -> {
                response.body.data.let { dataContainer ->
                    containerLiveData.value = dataContainer
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

    fun saveContainerRepair(container: Container, list: List<GenericSelectImageUiModel>) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = saveContainerRepairUseCase(createSaveModel(container, list))) {
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

    private fun createSaveModel(container: Container, list: List<GenericSelectImageUiModel>): SaveContainerRepairRequest {
        return SaveContainerRepairRequest(
            qrCode = qrCodeLiveData.value,
            containerCode = container.codeContainer,
            flagScan = flagScanLiveData.value?.type.orEmpty(),
            photo1 = getBase64(list, "1"),
            photo2 = getBase64(list, "2"),
            photo3 = getBase64(list, "3"),
            photo4 = getBase64(list, "4"),
            photo5 = getBase64(list, "5"),
            photo6 = getBase64(list, "6"),
            photo7 = getBase64(list, "7"),
            photo8 = getBase64(list, "8")
        )
    }


    private fun getBase64(pictures: List<GenericSelectImageUiModel>, position: String): String? {
        val filePath = pictures.firstOrNull { it.position == position }?.imageFilePath

        if (filePath.isNullOrBlank()) return null

        val file = File(filePath)

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }
}