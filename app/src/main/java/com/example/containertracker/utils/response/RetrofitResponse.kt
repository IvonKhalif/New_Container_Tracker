package com.example.containertracker.utils.response

import com.google.gson.annotations.SerializedName

data class RetrofitResponse<T>(@SerializedName("data") val data: T, @SerializedName("status") val status: String?)
data class RetrofitStatusResponse(@SerializedName("status") val status: String)
data class RetrofitURLResponse(@SerializedName("url") val url: String)
data class RetrofitListResponse<T>(@SerializedName("data") val data: List<T>)