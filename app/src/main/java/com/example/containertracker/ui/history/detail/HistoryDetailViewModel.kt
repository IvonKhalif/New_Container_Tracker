package com.example.containertracker.ui.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.containerladen.models.ContainerLadenDetail
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.domain.containerladen.GetContainerLadenHistoryUseCase
import com.example.containertracker.domain.history.GetHistoryUseCase
import com.example.containertracker.domain.history.history_detail.model.HistoryDetail
import com.example.containertracker.domain.localsales.usecase.GetHistoryLocalSalesUseCase
import com.example.containertracker.utils.enums.RoleAccessEnum
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class HistoryDetailViewModel(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val getHistoryLocalSalesUseCase: GetHistoryLocalSalesUseCase,
    private val getContainerLadenHistoryUseCase: GetContainerLadenHistoryUseCase
) : BaseViewModel() {

    private val _historyData = MutableLiveData<List<HistoryModel>>()
    val historyData: LiveData<List<HistoryModel>> get() = _historyData

    private val _historyContainerLadenData = MutableLiveData<ContainerLadenDetail>()
    val historyContainerLadenData: LiveData<ContainerLadenDetail> get() = _historyContainerLadenData

    var isFromContainerLaden = false

    fun getHistory(containerData: Container?, isContainerLaden: Boolean) {
        isFromContainerLaden = isContainerLaden
        val qrCode = containerData?.uniqueId.orEmpty()
        val batchId = containerData?.batchId.orEmpty()
        when {
            userData.value?.departmentId.orEmpty() == RoleAccessEnum.LOCALSALES.value ->
                getHistoryDetailLocalSales(qrCode, batchId)
            isContainerLaden -> getDetailHistoryLaden(containerData?.id.orEmpty())
            else -> getHistoryDetail(qrCode, batchId)
        }
    }

    private fun getHistoryDetail(qrCode: String, batchId: String) = viewModelScope.launch {
        showLoadingWidget()

        val requestParam = HistoryRequest(qrCode = qrCode, batchId = batchId)

        when (val response = getHistoryUseCase(requestParam)) {
            is NetworkResponse.Success -> {
                response.body.data.let {
                    _historyData.value = it
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

    private fun getHistoryDetailLocalSales(qrCode: String, batchId: String) = viewModelScope.launch {
        showLoadingWidget()

        val requestParam = HistoryRequest(qrCode = qrCode, batchId = batchId)

        when (val response = getHistoryLocalSalesUseCase(requestParam)) {
            is NetworkResponse.Success -> {
                response.body.data.let {
                    _historyData.value = it
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

    private fun getDetailHistoryLaden(trackingId: String) = viewModelScope.launch {
        showLoadingWidget()

        when (val response = getContainerLadenHistoryUseCase(trackingId)) {
            is NetworkResponse.Success -> {
                response.body.data.let {
                    _historyContainerLadenData.value = it
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

    fun getContainerConditionImage(historyModel: HistoryModel) = if (isFromContainerLaden) {
        HistoryDetail(
            backdoorCondition = historyModel.backDoorSideCondition.orEmpty(),
            backdoorConditionImage = historyModel.backDoorSideConditionImage.orEmpty(),
            frontCondition = historyModel.frontDoorSideCondition.orEmpty(),
            frontConditionImage = historyModel.frontDoorSideConditionImage.orEmpty(),
            roofCondition = historyModel.roofSideCondition.orEmpty(),
            roofConditionImage = historyModel.roofSideConditionImage.orEmpty(),
            floorCondition = historyModel.floorSideCondition.orEmpty(),
            floorConditionImage = historyModel.floorSideConditionImage.orEmpty(),
            rightCondition = historyModel.rightSideCondition.orEmpty(),
            rightConditionImage = historyModel.rightSideConditionImage.orEmpty(),
            leftCondition = historyModel.leftSideCondition.orEmpty(),
            leftConditionImage = historyModel.leftSideConditionImage.orEmpty()
            )
    } else null
}