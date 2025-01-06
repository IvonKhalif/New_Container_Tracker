package com.example.containertracker.data.user

import com.example.containertracker.data.user.models.User
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.*

interface UserService {
    @GET("v1/user/login")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun signIn(
        @Query("username") userName: String,
        @Query("password") password: String
    ): NetworkResponse<RetrofitResponse<User>, GenericErrorResponse>
}