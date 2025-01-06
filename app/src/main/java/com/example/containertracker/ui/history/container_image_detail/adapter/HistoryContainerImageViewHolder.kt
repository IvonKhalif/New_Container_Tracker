package com.example.containertracker.ui.history.container_image_detail.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.containertracker.R
import com.example.containertracker.databinding.ItemHistoryContainerImageBinding
import com.example.containertracker.ui.history.container_image_detail.models.HistoryConditionImageUiModel

/**
 * Created by yovi.putra on 21/07/22"
 * Project name: Container Tracker
 **/


class HistoryContainerImageViewHolder private constructor(
    private val binding: ItemHistoryContainerImageBinding,
    private val onClick: (HistoryConditionImageUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val context get() = binding.root.context
    private val circularProgressDrawable by lazy {
        CircularProgressDrawable(binding.root.context).also {
            it.strokeWidth = 5f
            it.centerRadius = 30f
            it.setColorSchemeColors(R.color.lightGreen)
            it.start()
        }
    }
    private var isImageError = false

    fun bind(data: HistoryConditionImageUiModel) = with(binding) {
        val imageUrl = data.imageFullUrl

        tvContainerImagePosition.text = data.positionName

        loadImage(source = imageUrl)

        root.setOnClickListener {
            if (isImageError) {
                loadImage(source = imageUrl)
            } else {
                onClick.invoke(data)
            }
        }
    }

    private fun loadImage(source: String) = with(binding) {
        Glide.with(context)
            .load(source)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_baseline_sync_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    isImageError = true
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    isImageError = false
                    return false
                }
            })
            .into(ivContainerImage)
    }

    companion object {

        fun create(
            viewGroup: ViewGroup,
            onClick: (HistoryConditionImageUiModel) -> Unit
        ): HistoryContainerImageViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemHistoryContainerImageBinding.inflate(inflater, viewGroup, false)
            return HistoryContainerImageViewHolder(binding = binding, onClick = onClick)
        }
    }
}