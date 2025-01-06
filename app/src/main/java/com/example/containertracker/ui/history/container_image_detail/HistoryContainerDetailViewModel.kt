package com.example.containertracker.ui.history.container_image_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.domain.history.history_detail.GetHistoryDetailUseCase
import com.example.containertracker.domain.history.history_detail.model.HistoryDetail
import com.example.containertracker.ui.history.container_image_detail.models.HistoryContainerDetailUiModel
import com.example.containertracker.ui.history.container_image_detail.models.asUiModel
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/


class HistoryContainerDetailViewModel(
    private val getHistoryDetail: GetHistoryDetailUseCase
) : BaseViewModel() {

    private val _historyData = MutableLiveData<HistoryDetail>()

    val conditionImageList: LiveData<HistoryContainerDetailUiModel> = _historyData.map {
        it.asUiModel()
    }

    fun initPage(id: Int, argsConditionImage: HistoryDetail?) {
        if (argsConditionImage != null) {
            argsConditionImage.let { _historyData.value = it }
        } else {
            onLoadDetail(id)
        }
    }

    private fun onLoadDetail(id: Int) = viewModelScope.launch {
        showLoadingWidget()

        when (val response = getHistoryDetail.invoke(trackingId = id)) {
            is NetworkResponse.Success -> {
                _historyData.value = response.body
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