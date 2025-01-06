package com.example.containertracker.ui.localsales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.domain.localsales.usecase.ScanLocalSalesContainerUseCase
import com.example.containertracker.domain.salesorder.GetSalesOrderNumberUseCase
import com.example.containertracker.utils.PostLiveData
import com.example.containertracker.utils.enums.FlagScanEnum
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanLocalSalesViewModel(
    private val containerUseCase: ScanLocalSalesContainerUseCase,
    private val getSalesOrderNumberUseCase: GetSalesOrderNumberUseCase
) : BaseViewModel() {
    var containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()
    val soNumberList = PostLiveData<List<SalesOrderNumber>?>()
    val voyageId = MutableLiveData<String>()
    val voyageIdIn = MutableLiveData<String>()
    val voyageIdOut = MutableLiveData<String>()

    fun getContainer(qrCode: String = "", flag: FlagScanEnum) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = containerUseCase(qrCode, containerCode.value, flag.type)) {
            is NetworkResponse.Success -> {
                response.body.data.let { dataContainer ->
                    when (val respSO = getSalesOrderNumberUseCase()) {
                        is NetworkResponse.Success -> {
                            soNumberList.value = respSO.body.data
                            containerLiveData.value = dataContainer
                        }
                        is NetworkResponse.ServerError -> {
                            _serverErrorState.value = respSO.body
                        }
                        is NetworkResponse.NetworkError -> {
                            _networkErrorState.value = respSO.error
                        }
                        is NetworkResponse.UnknownError -> _serverErrorState.value = respSO.body
                    }
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