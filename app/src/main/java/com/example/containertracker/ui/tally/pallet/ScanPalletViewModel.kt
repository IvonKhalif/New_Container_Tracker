package com.example.containertracker.ui.tally.pallet

import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.usecase.ScanPalletUseCase
import com.example.containertracker.domain.tally.usecase.SubmitStatusTallyUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanPalletViewModel(
    private val scanPalletUseCase: ScanPalletUseCase,
    private val submitStatusTallyUseCase: SubmitStatusTallyUseCase
): BaseViewModel() {

    val onSuccessScan = ActionLiveData()
    val onSuccessSubmit = ActionLiveData()

    fun scanPallet(barcode: String) {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = scanPalletUseCase(barcode, user?.id.orEmpty(), tallySheetId = "")) {
                is NetworkResponse.Success -> {
                    if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                        onSuccessScan.post()
                    } else {
                        _serverErrorState.value = GenericErrorResponse(
                            status = response.body.status,
                            message = response.body.toString()
                        )
                    }
                }
                is NetworkResponse.ServerError -> _serverErrorState.value = response.body
                is NetworkResponse.NetworkError -> _networkErrorState.value = response.error
                is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
            }
            hideLoadingWidget()
        }
    }

    fun finishProcess(idTallySheet: String) = viewModelScope.launch {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        showLoadingWidget()
        when(val response = submitStatusTallyUseCase(idTallySheet, user?.id)) {
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
            is NetworkResponse.ServerError -> _serverErrorState.value = response.body
            is NetworkResponse.NetworkError -> _networkErrorState.value = response.error
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
        hideLoadingWidget()
    }
}