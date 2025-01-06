package com.example.containertracker.ui.home.salesordernumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.R
import com.example.containertracker.data.salesorder.models.SalesOrderNumber
import com.example.containertracker.databinding.BottomSheetSelectSalesOrderNumberBinding
import com.example.containertracker.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SalesOrderNumberBottomSheet(
    private val onItemClick: (String) -> Unit
): BottomSheetDialogFragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: SalesOrderNumberViewModel by viewModel()
    private lateinit var binding: BottomSheetSelectSalesOrderNumberBinding
    private val salesOrderAdapter by lazy {
        SalesOrderNumberAdapter(::onItemClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_select_sales_order_number,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = parentFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun observer() {
        viewModel.apply {
            searchKeyword.observe(viewLifecycleOwner, ::handleSearch)
        }
    }

    private fun handleSearch(keyword: String?) {
        val soNumber =
        keyword?.let {
            mainViewModel.soNumberListLiveData.value.orEmpty()
                .filter { it.number.contains(keyword) }
        }
        updateSONumber(soNumber)
    }

    private fun initView() {
        val soNumberList = mainViewModel.soNumberListLiveData.value.orEmpty()
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                dialog.behavior.skipCollapsed = true
                sheet.parent.requestLayout()
            }
        }

        binding.recyclerSelectPost.adapter = salesOrderAdapter
        binding.recyclerSelectPost.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        updateSONumber(soNumberList)
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun updateSONumber(soNumberList: List<SalesOrderNumber>?) {
        salesOrderAdapter.items = soNumberList.orEmpty()
        binding.containerEmptyStateData.isVisible = soNumberList.isNullOrEmpty()
    }

    private fun onItemClicked(soNumber: SalesOrderNumber) {
        onItemClick(soNumber.number)
        dismiss()
    }
}
