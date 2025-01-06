package com.example.containertracker.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.containertracker.data.user.models.User
import com.example.containertracker.utils.PostLiveData
import com.example.containertracker.utils.UserUtil
import com.example.containertracker.utils.response.GenericErrorResponse
import kotlinx.coroutines.launch
import java.io.IOException

open class BaseViewModel: ViewModel() {
    @Deprecated("broken observe when save history")
    val genericErrorLiveData = PostLiveData<GenericErrorResponse?>()
    @Deprecated("broken observe when save history")
    val networkErrorLiveData = PostLiveData<IOException?>()
    @Deprecated("broken observe when save history")
    val loadingWidgetLiveData = PostLiveData<Boolean?>()

    protected val _serverErrorState = MutableLiveData<GenericErrorResponse>()
    val serverErrorState: LiveData<GenericErrorResponse> get() = _serverErrorState

    protected val _networkErrorState = MutableLiveData<IOException>()
    val networkErrorState: LiveData<IOException> get() = _networkErrorState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    val userData = MutableLiveData<User>(UserUtil.get())

    fun showLoadingWidget(){
        viewModelScope.launch {
            loadingWidgetLiveData.value = true
            _loadingState.value = true
        }
    }

    fun hideLoadingWidget(){
        viewModelScope.launch {
            loadingWidgetLiveData.value = false
            _loadingState.value = false
        }
    }
}