package com.example.containertracker.widget.select_image.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.containertracker.ui.home.containercondition.adapter.ContainerConditionImageViewHolder
import com.example.containertracker.ui.home.containercondition.models.ContainerImageUiModel

class GenericSelectImageAdapter(
    private val onClick: (imageFilePath: GenericSelectImageUiModel) -> Unit
) : ListAdapter<GenericSelectImageUiModel, GenericSelectImageViewHolder>(
    DIFF_ITEM_CALLBACK
) {
    companion object {
        private val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<GenericSelectImageUiModel>() {
            override fun areItemsTheSame(
                oldItem: GenericSelectImageUiModel,
                newItem: GenericSelectImageUiModel
            ): Boolean = oldItem.position == newItem.position
                    && oldItem.imageFilePath == newItem.imageFilePath

            override fun areContentsTheSame(
                oldItem: GenericSelectImageUiModel,
                newItem: GenericSelectImageUiModel
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericSelectImageViewHolder {
        return GenericSelectImageViewHolder.create(viewGroup = parent, onClick = onClick)
    }

    override fun onBindViewHolder(holder: GenericSelectImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}