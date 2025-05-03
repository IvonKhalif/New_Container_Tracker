package com.example.containertracker.ui.tally.draft

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.R
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.ItemContainerBinding

class TallyContainerDraftAdapter(
    _items: List<TallySheetDrafts>,
    private val onItemClick: (TallySheetDrafts) -> Unit,
    private val onDeleteClick: (TallySheetDrafts) -> Unit
): RecyclerView.Adapter<TallyContainerDraftAdapter.ViewHolder>() {
    var items = _items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemContainerBinding,
        private val onClick: (TallySheetDrafts) -> Unit,
        private val onDeleteClick: (TallySheetDrafts) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TallySheetDrafts) {
            binding.apply {
                containerName.text = item.codeContainer
                root.setOnClickListener {
                    onClick(item)
                }
                buttonDelete.isVisible = true
                buttonDelete.setOnClickListener {
                    onDeleteClick(item)
                }
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClicked: (TallySheetDrafts) -> Unit,
                onDeleteClicked: (TallySheetDrafts) -> Unit
            ): ViewHolder {
                val view = DataBindingUtil
                    .inflate<ItemContainerBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_container,
                        parent,
                        false
                    )
                return ViewHolder(view, onItemClicked, onDeleteClicked)
            }
        }
    }
}