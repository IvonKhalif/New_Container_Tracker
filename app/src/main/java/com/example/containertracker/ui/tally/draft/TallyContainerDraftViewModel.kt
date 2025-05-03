package com.example.containertracker.ui.tally.draft

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.usecase.DeleteTallySheetUseCase
import com.example.containertracker.domain.tally.usecase.GetTallyDraftListUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class TallyContainerDraftViewModel(
    private val draftListUseCase: GetTallyDraftListUseCase,
    private val deleteTallySheetUseCase: DeleteTallySheetUseCase
) : BaseViewModel() {

    var draftsLiveData = MutableLiveData<List<TallySheetDrafts>>()

    val onDeleted = ActionLiveData()

    fun getDrafts() = viewModelScope.launch {
        showLoadingWidget()
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        when (val response = draftListUseCase(user?.id.orEmpty())) {
            is NetworkResponse.Success -> {
                response.body.data.let {
                    draftsLiveData.value = it
                    hideLoadingWidget()
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

    fun deleteContainer(idTallySheet: String) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = deleteTallySheetUseCase(idTallySheet)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onDeleted.postAction()
                } else {
                    _serverErrorState.value = GenericErrorResponse(
                        status = response.body.status,
                        message = response.body.toString()
                    )
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