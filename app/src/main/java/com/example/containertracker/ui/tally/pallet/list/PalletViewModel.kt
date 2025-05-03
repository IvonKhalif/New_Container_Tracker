package com.example.containertracker.ui.tally.pallet.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.usecase.DeletePalletUseCase
import com.example.containertracker.domain.tally.usecase.GetPalletsUseCase
import com.example.containertracker.domain.tally.usecase.SubmitStatusTallyUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class PalletViewModel(
    private val getPalletsUseCase: GetPalletsUseCase,
    private val deletePalletUseCase: DeletePalletUseCase,
    private val submitStatusTallyUseCase: SubmitStatusTallyUseCase
): BaseViewModel() {

    val palletsData = MutableLiveData<List<PalletResponse>>()

    val onDeleted = ActionLiveData()
    val onFinished = ActionLiveData()

    fun getPallets(idTallySheet: String) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getPalletsUseCase(idTallySheet)) {
            is NetworkResponse.Success -> {
                response.body.data.let {
                    palletsData.value = it
                    hideLoadingWidget()
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

    fun deletePallet(idTallySheetPallet: String) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = deletePalletUseCase(idTallySheetPallet)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onDeleted.postAction()
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

    fun finishProcess(idTallySheet: String) = viewModelScope.launch {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        showLoadingWidget()
        when(val response = submitStatusTallyUseCase(idTallySheet, user?.id)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onFinished.post()
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