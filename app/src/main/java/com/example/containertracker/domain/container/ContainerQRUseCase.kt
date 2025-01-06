package com.example.containertracker.domain.container

class ContainerQRUseCase(private val repository: ContainerRepository) {
    suspend operator fun invoke(
        id: String
    ) = repository.getQRContainer(id)
}