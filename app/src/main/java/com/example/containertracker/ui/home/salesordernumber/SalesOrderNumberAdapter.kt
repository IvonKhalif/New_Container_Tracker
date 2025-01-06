package com.example.containertracker.ui.home.salesordernumber

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.databinding.ItemSelectPostBinding
import com.example.containertracker.ui.selectlocation.SelectLocationAdapter

class SalesOrderNumberAdapter(
    private val onItemClick: (SalesOrderNumber) -> Unit
): RecyclerView.Adapter<SalesOrderNumberAdapter.ViewHolder>() {
    var items = listOf<SalesOrderNumber>()
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
        private val onItemClick: (SalesOrderNumber) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SalesOrderNumber) {
            binding.apply {
                tvTitlePost.text = item.number
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClick: (SalesOrderNumber) -> Unit
            ): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val viewDataBinding = ItemSelectPostBinding.inflate(inflater, parent, false)
                return ViewHolder(viewDataBinding, onItemClick)
            }
        }
    }
}