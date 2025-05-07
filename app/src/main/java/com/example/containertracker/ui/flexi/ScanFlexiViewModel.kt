package com.example.containertracker.ui.flexi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.domain.flexi.usecase.GetContainerFlexiUseCase
import com.example.containertracker.domain.localsales.usecase.ScanFlexiLocalSalesUseCase
import com.example.containertracker.utils.UserUtil
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanFlexiViewModel(
    private val getContainerFlexiUseCase: GetContainerFlexiUseCase,
    private val getFlexiLocalSalesUseCase: ScanFlexiLocalSalesUseCase
) : BaseViewModel() {
    var containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()

    fun getContainer(qrCode: String = "", flag: FlagScanEnum) {
        val isLocalSales = UserUtil.getDepartmentId() == RoleAccessEnum.LOCALSALES.value
        if (isLocalSales) scanFlexiLocalSales(qrCode, flag) else scanFlexiRegular(qrCode, flag)
    }

    private fun scanFlexiRegular(qrCode: String = "", flag: FlagScanEnum) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getContainerFlexiUseCase(qrCode = containerCode.value.orEmpty())) {
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

    private fun scanFlexiLocalSales(qrCode: String = "", flag: FlagScanEnum) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getFlexiLocalSalesUseCase(qrCode, containerCode.value, flag.type)) {
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