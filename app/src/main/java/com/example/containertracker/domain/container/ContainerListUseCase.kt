package com.example.containertracker.domain.container

class ContainerListUseCase(private val repository: ContainerRepository) {
    suspend operator fun invoke(keyword: String?) = repository.getList(keyword)
}