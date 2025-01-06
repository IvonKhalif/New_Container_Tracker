package com.example.containertracker.ui.login

import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.user.SignInUseCase
import com.example.containertracker.utils.PostLiveData
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val signInUseCase: SignInUseCase): BaseViewModel() {
    val loginLiveData = PostLiveData<User?>()

    fun signIn(username: String, password: String) {
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = signInUseCase(username, password)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
//                        UserUtil.setToken(it.token)
//                        UserUtil.update(it)
                        hideLoadingWidget()
                        loginLiveData.post(it)
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