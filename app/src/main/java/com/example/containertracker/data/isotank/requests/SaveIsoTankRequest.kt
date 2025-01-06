package com.example.containertracker.data.isotank.requests

import com.google.gson.annotations.SerializedName

data class SaveIsoTankRequest(
    @SerializedName("id_sales_order_detail") var salesOrderDetailId: String?,
    @SerializedName("account") val userId: String? = null,
    @SerializedName("tarra") var isoTankTarra: String?,
    @SerializedName("cap") var isoTankCap: String?,
    @SerializedName("remark") var containerRemark: String?,
    @SerializedName("inner_tank") var statusInnerTank: String?,
    @SerializedName("man_hole") var statusManHole: String?,
    @SerializedName("manifold") var statusManifold: String?,
)
