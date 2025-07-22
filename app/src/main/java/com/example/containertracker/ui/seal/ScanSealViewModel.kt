package com.example.containertracker.ui.seal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.domain.localsales.usecase.ScanSealLocalSalesUseCase
import com.example.containertracker.domain.seal.usecase.GetContainerSealUseCase
import com.example.containertracker.utils.UserUtil
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanSealViewModel(
    private val getContainerSealUseCase: GetContainerSealUseCase,
    private val scanSealLocalSalesUseCase: ScanSealLocalSalesUseCase
) : BaseViewModel() {
    var containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()

    fun getContainer(qrCode: String = "", flag: FlagScanEnum) {
        val isLocalSales = UserUtil.getDepartmentId() == RoleAccessEnum.LOCALSALES.value
        if (isLocalSales) scanSealLocalSales(qrCode, flag) else scanSealRegular(qrCode)
    }

    private fun scanSealRegular(qrCode: String) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getContainerSealUseCase(qrCode)) {
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

    private fun scanSealLocalSales(qrCode: String = "", flag: FlagScanEnum) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = scanSealLocalSalesUseCase(qrCode, containerCode.value, flag.type)) {
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
}