package com.example.containertracker.utils.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class GenericErrorResponse(
    @SerializedName("error_code")
    val errorCode: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("desc")
    val desc: String? = ""
): Parcelable {
    companion object{
        fun generalError() = GenericErrorResponse(
            errorCode = "100",
            status = "Error Status",
            message = "Error"
        )
    }

    fun isAlreadySignUp(): Boolean{
        return errorCode == "API-0011" || errorCode == "API-0012"
    }
}