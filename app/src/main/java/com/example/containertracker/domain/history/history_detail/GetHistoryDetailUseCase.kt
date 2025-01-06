package com.example.containertracker.domain.history.history_detail

import com.example.containertracker.domain.history.HistoryRepository

/**
 * Created by yovi.putra on 24/07/22"
 * Project name: Container Tracker
 **/


class GetHistoryDetailUseCase(
    private val repository: HistoryRepository
) {

    suspend operator fun invoke(trackingId: Int) =
        repository.getHistoryDetail(trackingId = trackingId)
}