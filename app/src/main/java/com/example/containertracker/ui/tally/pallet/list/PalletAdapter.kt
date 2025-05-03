package com.example.containertracker.ui.tally.pallet.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.R
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.databinding.ItemPalletBinding

class PalletAdapter(
    _items: List<PalletResponse>,
    private val onEditClick: (PalletResponse) -> Unit,
    private val onDeleteClick: (PalletResponse) -> Unit,
): RecyclerView.Adapter<PalletAdapter.ViewHolder>() {
    var items = _items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemPalletBinding,
        private val onEditClick: (PalletResponse) -> Unit,
        private val onDeleteClick: (PalletResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PalletResponse) {
            binding.apply {
                palletName.text = item.palletId
                batchNumber.text = root.context.getString(R.string.pallet_list_batch_label, item.batchNumber)
                buttonEdit.setOnClickListener {
                    onEditClick(item)
                }
                buttonDelete.setOnClickListener {
                    onDeleteClick(item)
                }
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onEditClicked: (PalletResponse) -> Unit,
                onDeleteClick: (PalletResponse) -> Unit
            ): ViewHolder {
                val view = DataBindingUtil
                    .inflate<ItemPalletBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_pallet,
                        parent,
                        false
                    )
                return ViewHolder(view, onEditClicked, onDeleteClick)
            }
        }
    }
}