package com.example.containertracker.ui.history

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.widget.select_image.DiffUtilItemCallback

class HistoryAdapter(
    private val isHistoryDetail: Boolean = false,
    private val onItemClick: (HistoryModel) -> Unit
) : ListAdapter<HistoryModel, HistoryViewHolder>(DIFF_ITEM_CALLBACK) {
    
    companion object {
        private val DIFF_ITEM_CALLBACK = DiffUtilItemCallback<HistoryModel>(
            isItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            isContentTheSame = {oldItem, newItem -> oldItem == newItem }
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.create(parent, isHistoryDetail, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}