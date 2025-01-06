package com.example.containertracker.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.domain.location.LocationsLocalSalesUseCase
import com.example.containertracker.domain.location.LocationsUseCase
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.example.containertracker.utils.extension.orFalse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class MainViewModel(
    private val locationsUseCase: LocationsUseCase,
    private val locationsLocalSalesUseCase: LocationsLocalSalesUseCase
) : BaseViewModel() {
    val locationLiveData = MutableLiveData<Location>()
    val locationListLiveData = MutableLiveData<List<Location>>()
    val soNumberListLiveData = MutableLiveData<List<SalesOrderNumber>>()
    val isContainerLaden = MutableLiveData<Boolean>().apply { value = false }

    fun getLocations(userId: String) {
        if (userData.value?.departmentId.orEmpty() == RoleAccessEnum.LOCALSALES.value)
            getLocationsLocalSales(userId)
        else
            getLocationList(userId)
    }

    private fun getLocationList(userId: String) {
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = locationsUseCase(userId, isContainerLaden.value.orFalse())) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        locationListLiveData.value = it
                    }
                }
                is NetworkResponse.ServerError -> {
                    genericErrorLiveData.value = response.body
                }
                is NetworkResponse.NetworkError -> {
                    networkErrorLiveData.value = response.error
                }
                is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
            }
            hideLoadingWidget()
        }
    }

    private fun getLocationsLocalSales(userId: String) {
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = locationsLocalSalesUseCase(userId)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        locationListLiveData.value = it
                    }
                }
                is NetworkResponse.ServerError -> {
                    genericErrorLiveData.value = response.body
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