package com.example.containertracker.ui.history.container_image_detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.containertracker.ui.history.container_image_detail.models.HistoryConditionImageUiModel
import com.example.containertracker.widget.select_image.DiffUtilItemCallback

/**
 * Created by yovi.putra on 21/07/22"
 * Project name: Container Tracker
 **/


class HistoryContainerImageAdapter(
    private val onClick: (HistoryConditionImageUiModel) -> Unit
) : ListAdapter<HistoryConditionImageUiModel, HistoryContainerImageViewHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        private val DIFF_ITEM_CALLBACK = DiffUtilItemCallback<HistoryConditionImageUiModel>(
            isContentTheSame = { oldItem, newItem -> oldItem == newItem },
            isItemsTheSame = { oldItem, newItem ->
                oldItem.position == newItem.position
                        && oldItem.imageUrl == newItem.imageUrl
                        && oldItem.condition == newItem.condition }
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryContainerImageViewHolder {
        return HistoryContainerImageViewHolder.create(viewGroup = parent, onClick = onClick)
    }

    override fun onBindViewHolder(holder: HistoryContainerImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}