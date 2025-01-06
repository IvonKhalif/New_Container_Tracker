package com.example.containertracker.ui.home.containercondition.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.containertracker.R
import com.example.containertracker.databinding.ItemContainerImageBinding
import com.example.containertracker.ui.home.containercondition.models.ContainerImageUiModel

/**
 * Created by yovi.putra on 21/07/22"
 * Project name: Container Tracker
 **/


class ContainerConditionImageViewHolder private constructor(
    private val binding: ItemContainerImageBinding,
    private val onClick: (ContainerImageUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val context get() = binding.root.context

    fun bind(data: ContainerImageUiModel) = with(binding) {

        tvContainerImagePosition.text = data.positionName

        if (data.imageFilePath != null) {
            Glide.with(context)
                .load(data.imageFilePath)
                .placeholder(R.color.background_F3F3F3)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivContainerImage)
        }

        root.setOnClickListener {
            onClick.invoke(data)
        }
    }

    companion object {

        fun create(
            viewGroup: ViewGroup,
            onClick: (ContainerImageUiModel) -> Unit
        ): ContainerConditionImageViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemContainerImageBinding.inflate(inflater, viewGroup, false)
            return ContainerConditionImageViewHolder(binding = binding, onClick = onClick)
        }
    }
}