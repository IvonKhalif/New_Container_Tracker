package com.example.containertracker.domain.salesorder

import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface SalesOrderNumberRepository {
    suspend fun getSalesOrderNumber(): NetworkResponse<RetrofitResponse<List<SalesOrderNumber>>, GenericErrorResponse>
}