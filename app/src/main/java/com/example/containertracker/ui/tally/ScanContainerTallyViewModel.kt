package com.example.containertracker.ui.tally

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.domain.flexi.usecase.GetContainerFlexiUseCase
import com.example.containertracker.utils.enums.FlagScanEnum
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanContainerTallyViewModel(
    private val getContainerFlexiUseCase: GetContainerFlexiUseCase
) : BaseViewModel() {
    val containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()

    fun scanContainer(qrCode: String = "", flag: FlagScanEnum) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getContainerFlexiUseCase(qrCode, containerCode.value, flag.type)) {
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
    }
}