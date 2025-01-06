package com.example.containertracker.domain.user

import com.example.containertracker.data.user.models.User
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface UserRepository {
    suspend fun signIn(
        userName: String,
        password: String
    ): NetworkResponse<RetrofitResponse<User>, GenericErrorResponse>
}