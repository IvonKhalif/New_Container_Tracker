package com.example.containertracker.domain.salesorder

class GetSalesOrderNumberUseCase(private val repository: SalesOrderNumberRepository) {
    suspend operator fun invoke() = repository.getSalesOrderNumber()
}