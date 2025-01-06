package com.example.containertracker.ui.home.containercondition.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.containertracker.ui.home.containercondition.models.ContainerImageUiModel

/**
 * Created by yovi.putra on 21/07/22"
 * Project name: Container Tracker
 **/


class ContainerConditionImageAdapter(
    private val onClick: (ContainerImageUiModel) -> Unit
) :
    ListAdapter<ContainerImageUiModel, ContainerConditionImageViewHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        private val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<ContainerImageUiModel>() {
            override fun areItemsTheSame(
                oldItem: ContainerImageUiModel,
                newItem: ContainerImageUiModel
            ): Boolean = oldItem.position == newItem.position
                    && oldItem.imageFilePath == newItem.imageFilePath
                    && oldItem.condition == newItem.condition

            override fun areContentsTheSame(
                oldItem: ContainerImageUiModel,
                newItem: ContainerImageUiModel
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContainerConditionImageViewHolder {
        return ContainerConditionImageViewHolder.create(viewGroup = parent, onClick = onClick)
    }

    override fun onBindViewHolder(holder: ContainerConditionImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}