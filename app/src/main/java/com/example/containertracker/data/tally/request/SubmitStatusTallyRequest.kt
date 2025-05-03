package com.example.containertracker.data.tally.request

import com.google.gson.annotations.SerializedName

data class SubmitStatusTallyRequest(
    @SerializedName("id_tally_sheet") var idTallySheet: String?,
    @SerializedName("id_account") var accountId: String?,
)
