package com.example.containertracker.data.salesorder

import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.domain.salesorder.SalesOrderNumberRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.haroldadmin.cnradapter.NetworkResponse

class SalesOrderNumberRepositoryImpl(
    private val service: SalesOrderService
) : SalesOrderNumberRepository {
    override suspend fun getSalesOrderNumber(): NetworkResponse<RetrofitResponse<List<SalesOrderNumber>>, GenericErrorResponse> =
        service.getSONumberList()
}