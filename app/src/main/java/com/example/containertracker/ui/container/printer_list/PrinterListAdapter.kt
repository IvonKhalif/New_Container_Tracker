package com.example.containertracker.ui.container.printer_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.databinding.ItemPrinterBinding
import com.example.containertracker.utils.printer.PrinterUiModel

/**
 * Created by yovi.putra on 18/07/22"
 * Project name: Container Tracker
 **/


class PrinterListAdapter(
    private val onClick: (PrinterUiModel) -> Unit
) : ListAdapter<PrinterUiModel, PrinterListAdapter.ViewHolder>(DIFF_ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent = parent, onClick = onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(getItem(position))
    }

    class ViewHolder(
        private val view: ItemPrinterBinding,
        private val onClick: (PrinterUiModel) -> Unit
    ) : RecyclerView.ViewHolder(view.root) {

        fun binding(printer: PrinterUiModel) {
            view.tvTitle.text = printer.printerName

            view.root.setOnClickListener {
                onClick.invoke(printer)
            }
        }

        companion object {

            fun create(parent: ViewGroup, onClick: (PrinterUiModel) -> Unit): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPrinterBinding.inflate(inflater, parent, false)
                return ViewHolder(view = binding, onClick = onClick)
            }
        }
    }

    companion object {
        private val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<PrinterUiModel>() {
            override fun areItemsTheSame(oldItem: PrinterUiModel, newItem: PrinterUiModel): Boolean {
                return oldItem.address == newItem.address
            }

            override fun areContentsTheSame(oldItem: PrinterUiModel, newItem: PrinterUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}