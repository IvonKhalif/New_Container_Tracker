package com.example.containertracker.data.tally.request

import com.google.gson.annotations.SerializedName

data class SavePhotoTallyRequest(
    @SerializedName("id_tally_sheet") var idTallySheet: String?,
    @SerializedName("item_id") var itemId: String?,
    @SerializedName("photo") var photo: String?
)
