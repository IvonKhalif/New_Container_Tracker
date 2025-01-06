package com.example.containertracker.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.domain.container.ContainerUseCase
import com.example.containertracker.domain.salesorder.GetSalesOrderNumberUseCase
import com.example.containertracker.ui.home.containerdefect.ContainerDefectParam
import com.example.containertracker.utils.PostLiveData
import com.example.containertracker.utils.enums.ContainerRejectionStatusEnum
import com.example.containertracker.utils.enums.FlagScanEnum
import com.example.containertracker.utils.extension.orFalse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class HomeViewModel(
    private val containerUseCase: ContainerUseCase,
    private val getSalesOrderNumberUseCase: GetSalesOrderNumberUseCase
) : BaseViewModel() {

    var containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()
    val voyageId = MutableLiveData<String>()
    val voyageIdIn = MutableLiveData<String>()
    val voyageIdOut = MutableLiveData<String>()
    val soNumberList = PostLiveData<List<SalesOrderNumber>?>()
    val rejectionStatus: MutableLiveData<ContainerRejectionStatusEnum> =
        MutableLiveData<ContainerRejectionStatusEnum>().apply { value = ContainerRejectionStatusEnum.RELEASE }
    val isContainerLaden = MutableLiveData<Boolean>().apply { value = false }

    //Container Defect Data
    var containerDefectParam: ContainerDefectParam? = null

    fun getContainer(qrCode: String = "", flag: FlagScanEnum) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = containerUseCase(qrCode, containerCode.value, flag.type, isContainerLaden.value.orFalse())) {
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