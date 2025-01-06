package com.example.containertracker.ui.selectlocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.databinding.ItemSelectPostBinding

class SelectLocationAdapter(
    _items: List<Location>,
    private val onItemClick: (Location) -> Unit
): RecyclerView.Adapter<SelectLocationAdapter.ViewHolder>() {
    var items = _items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemSelectPostBinding,
        private val onItemClick: (Location) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Location) {
            binding.apply {
                tvTitlePost.text = item.name
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClick: (Location) -> Unit
            ): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val viewDataBinding = ItemSelectPostBinding.inflate(inflater, parent, false)
                return ViewHolder(viewDataBinding, onItemClick)
            }
        }
    }
}