package com.example.containertracker.domain.containerladen

class GetContainerLadenHistoryUseCase(private val repository: ContainerLadenRepository) {
    suspend operator fun invoke(idTracking: String) =
        repository.getHistory(idTracking)
}