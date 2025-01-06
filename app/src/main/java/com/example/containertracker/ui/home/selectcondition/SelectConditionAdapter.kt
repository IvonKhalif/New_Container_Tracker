package com.example.containertracker.ui.home.selectcondition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.databinding.ItemSelectPostBinding
import com.example.containertracker.utils.enums.ConditionEnum

class SelectConditionAdapter(
    private val onItemClick: (ConditionEnum) -> Unit
) : RecyclerView.Adapter<SelectConditionAdapter.ViewHolder>() {
    private var items = ConditionEnum.values()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemSelectPostBinding,
        private val onItemClick: (ConditionEnum) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConditionEnum) {
            binding.apply {
                tvTitlePost.text = item.type
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClick: (ConditionEnum) -> Unit
            ): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val viewDataBinding = ItemSelectPostBinding.inflate(inflater, parent, false)
                return ViewHolder(viewDataBinding, onItemClick)
            }
        }
    }
}