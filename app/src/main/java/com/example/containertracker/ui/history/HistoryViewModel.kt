package com.example.containertracker.ui.history

import androidx.lifecycle.*
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.data.history.requests.HistoryRequest
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.history.GetHistoryUseCase
import com.example.containertracker.utils.DateUtil
import com.example.containertracker.utils.PostLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class HistoryViewModel(private val getHistoryUseCase: GetHistoryUseCase) : BaseViewModel() {

    var startDate = MutableLiveData(LocalDate.now())
    var startDateDisplay: LiveData<String> = startDate.map {
        when (it) {
            null -> ""
            LocalDate.now() -> "Today"
            else -> DateUtil.formatLocalDateToString(it, "dd MMM yyyy")
        }
    }

    var endDate = MutableLiveData(LocalDate.now())
    var endDateDisplay: LiveData<String> = endDate.map {
        when (it) {
            null -> ""
            LocalDate.now() -> "Today"
            else -> DateUtil.formatLocalDateToString(it, "dd MMM yyyy")
        }
    }

    var keywordSearch = MutableLiveData("")
    var containerData = MutableLiveData<Container>()
    val historyListLiveData = PostLiveData<List<HistoryModel>?>()

    init {
        getHistory()
    }

    fun getHistory() {
        showLoadingWidget()
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        val startDateString = startDate.value?.let { DateUtil.formatLocalDateToString(it, "dd/MM/yyyy") }
        val endDateString = endDate.value?.let { DateUtil.formatLocalDateToString(it, "dd/MM/yyyy") }
        val requestParam = HistoryRequest(
            qrCode = containerData.value?.uniqueId.orEmpty(),
            userId = user?.id.orEmpty(),
            startDate = startDateString,
            endDate = endDateString,
            containerCode = keywordSearch.value.orEmpty()
        )
        viewModelScope.launch {
            when (val response = getHistoryUseCase(requestParam)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        hideLoadingWidget()
                        historyListLiveData.post(it)
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