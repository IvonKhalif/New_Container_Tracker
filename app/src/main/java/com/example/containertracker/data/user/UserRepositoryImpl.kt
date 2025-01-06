package com.example.containertracker.data.user

import android.content.Context
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.user.UserRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse

class UserRepositoryImpl(
    private val userService: UserService
): UserRepository {
    override suspend fun signIn(
        userName: String,
        password: String
    ): NetworkResponse<RetrofitResponse<User>, GenericErrorResponse> {
        return userService.signIn(userName, password)
    }
}