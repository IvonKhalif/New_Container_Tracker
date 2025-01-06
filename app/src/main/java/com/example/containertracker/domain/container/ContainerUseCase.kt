package com.example.containertracker.domain.container

import com.example.containertracker.domain.containerladen.ContainerLadenRepository

class ContainerUseCase(
    private val repository: ContainerRepository,
    private val containerLadenRepository: ContainerLadenRepository
) {
    suspend operator fun invoke(
        qrCode: String,
        containerCode: String?,
        flag: String,
        isContainerLaden: Boolean = false
    ) = when (isContainerLaden){
        false -> repository.getContainer(qrCode, containerCode, flag)
        true -> containerLadenRepository.getContainer(qrCode, containerCode, flag)
    }
}