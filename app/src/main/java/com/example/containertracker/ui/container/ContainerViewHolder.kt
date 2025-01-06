package com.example.containertracker.ui.container

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.R
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.databinding.ItemContainerBinding
import com.example.containertracker.utils.DateUtil
import com.example.containertracker.utils.enums.ConditionEnum
import org.threeten.bp.LocalDateTime

class ContainerViewHolder(
    val binding: ItemContainerBinding,
    private val onItemClick: (ContainerDetail) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ContainerDetail) {
        binding.apply {
            containerName.text = item.codeContainer
            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (ContainerDetail) -> Unit
        ): ContainerViewHolder {
            val view = DataBindingUtil
                .inflate<ItemContainerBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_container,
                    parent,
                    false
                )
            return ContainerViewHolder(view, onItemClicked)
        }
    }
}