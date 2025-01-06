package com.example.containertracker.domain.repair

import com.example.containertracker.data.repair.request.SaveContainerRepairRequest

class SaveContainerRepairUseCase(private val repository: ContainerRepairRepository) {
    suspend operator fun invoke(
        request: SaveContainerRepairRequest
    ) = repository.saveRepair(request)
}