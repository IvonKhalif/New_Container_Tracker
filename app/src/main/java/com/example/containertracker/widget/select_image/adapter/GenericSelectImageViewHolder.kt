package com.example.containertracker.widget.select_image.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.containertracker.R
import com.example.containertracker.databinding.ItemContainerImageBinding
import com.example.containertracker.ui.home.containercondition.models.ContainerImageUiModel

class GenericSelectImageViewHolder private constructor(
    private val binding: ItemContainerImageBinding,
    private val onClick: (imageFilePath: GenericSelectImageUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val context get() = binding.root.context

    fun bind(item: GenericSelectImageUiModel) = with(binding) {
        tvContainerImagePosition.isVisible = !item.positionName.isNullOrBlank()
        tvContainerImagePosition.text = item.positionName
        if (item.imageFilePath != null) {
            Glide.with(context)
                .load(item.imageFilePath)
                .placeholder(R.color.background_F3F3F3)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivContainerImage)
        } else {
            ivContainerImage.setBackgroundColor(ContextCompat.getColor(context, R.color.background_F3F3F3))
            ivContainerImage.setImageResource(R.drawable.ic_baseline_add_circle_outline_56)
        }

        root.setOnClickListener {
            onClick.invoke(item)
        }
    }

    companion object {

        fun create(
            viewGroup: ViewGroup,
            onClick: (imageFilePath: GenericSelectImageUiModel) -> Unit
        ): GenericSelectImageViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemContainerImageBinding.inflate(inflater, viewGroup, false)
            return GenericSelectImageViewHolder(binding = binding, onClick = onClick)
        }
    }
}