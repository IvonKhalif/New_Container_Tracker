package com.example.containertracker.ui.home.selectcondition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.R
import com.example.containertracker.databinding.ItemSelectPostBinding
import com.example.containertracker.utils.enums.ConditionEnum

class SelectConditionAdapter(
    private val isContainerTally: Boolean = false,
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
        return ViewHolder.create(parent, isContainerTally, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemSelectPostBinding,
        private val isContainerTally: Boolean = false,
        private val onItemClick: (ConditionEnum) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConditionEnum) {
            binding.apply {
                tvTitlePost.text = if (item == ConditionEnum.GOOD && isContainerTally )
                    root.context.getString(R.string.tally_sheet_container_condition_good)
                else
                    item.type
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                isContainerTally: Boolean = false,
                onItemClick: (ConditionEnum) -> Unit
            ): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val viewDataBinding = ItemSelectPostBinding.inflate(inflater, parent, false)
                return ViewHolder(viewDataBinding, isContainerTally,onItemClick)
            }
        }
    }
}