package com.example.containertracker.ui.container

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.ui.history.HistoryViewHolder

class ContainerAdapter(
    _items: List<ContainerDetail>,
    private val onItemClick: (ContainerDetail) -> Unit
): RecyclerView.Adapter<ContainerViewHolder>() {
    var items = _items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerViewHolder {
        return ContainerViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: ContainerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}