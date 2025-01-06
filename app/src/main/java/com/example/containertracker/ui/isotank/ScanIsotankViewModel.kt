package com.example.containertracker.ui.isotank

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.domain.flexi.usecase.GetContainerFlexiUseCase
import com.example.containertracker.domain.isotank.usecase.GetContainerIsoTankUseCase
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class ScanIsotankViewModel(
    private val getContainerIsoTankUseCase: GetContainerIsoTankUseCase
) : BaseViewModel() {
    var containerCode = MutableLiveData("")
    val containerLiveData = MutableLiveData<Container>()

    fun getContainer(qrCode: String = "") = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getContainerIsoTankUseCase(qrCode)) {
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