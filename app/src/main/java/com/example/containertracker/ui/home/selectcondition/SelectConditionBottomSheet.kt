package com.example.containertracker.ui.home.selectcondition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.R
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.databinding.BottomSheetSelectConditionBinding
import com.example.containertracker.ui.selectlocation.SelectLocationAdapter
import com.example.containertracker.utils.enums.ConditionEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectConditionBottomSheet(
    private val isContainerTally: Boolean = false,
    private val onItemClick: (ConditionEnum) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSelectConditionBinding
    private val conditionAdapter by lazy {
        SelectConditionAdapter(isContainerTally, ::onItemClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_select_condition, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.recyclerSelectCondition.adapter = conditionAdapter
        binding.recyclerSelectCondition.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun onItemClicked(conditionEnum: ConditionEnum) {
        onItemClick(conditionEnum)
        dismiss()
    }

}