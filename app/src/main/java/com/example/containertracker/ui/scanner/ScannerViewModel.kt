package com.example.containertracker.ui.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.domain.container.ContainerUseCase
import com.example.containertracker.utils.enums.FlagScanEnum
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val containerUseCase: ContainerUseCase
) : BaseViewModel() {
    val containerLiveData = MutableLiveData<Container>()

    fun getContainer(qrCode: String) {
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = containerUseCase(qrCode, null, FlagScanEnum.SCAN.type)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        containerLiveData.value = it
                    }
                }
                is NetworkResponse.ServerError -> {
                    genericErrorLiveData.value = response.body
                    hideLoadingWidget()
                }
                is NetworkResponse.NetworkError -> {
                    networkErrorLiveData.value = response.error
                }
                is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
            }
            hideLoadingWidget()
        }
    }
}