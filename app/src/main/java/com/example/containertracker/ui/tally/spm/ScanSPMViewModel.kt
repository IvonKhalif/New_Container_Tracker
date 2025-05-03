package com.example.containertracker.ui.tally.spm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.usecase.ScanSPMUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanSPMViewModel(
    private val scanSPMUseCase: ScanSPMUseCase
): BaseViewModel() {
    val spmNumber = MutableLiveData("")
    val onSuccessScan = ActionLiveData()

    fun scanSPM(barcode: String, idTallySheet: String, flag: FlagScanEnum) {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = scanSPMUseCase(barcode, user?.id.orEmpty(), flag = flag.type, tallySheetId = idTallySheet)) {
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
}