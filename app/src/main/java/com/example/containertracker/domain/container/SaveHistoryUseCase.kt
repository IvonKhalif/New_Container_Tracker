package com.example.containertracker.domain.container

import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.domain.containerladen.ContainerLadenRepository

class SaveHistoryUseCase(
    private val repository: ContainerRepository,
    private val containerLadenRepository: ContainerLadenRepository
) {
    suspend operator fun invoke(
        request: SaveContainerHistoryRequest,
        isContainerLaden: Boolean = false
    ) = when (isContainerLaden){
        false -> repository.saveHistory(request)
        true -> containerLadenRepository.saveHistory(request)
    }
}