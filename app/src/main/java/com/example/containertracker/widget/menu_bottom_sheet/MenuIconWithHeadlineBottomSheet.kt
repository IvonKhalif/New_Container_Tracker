package com.example.containertracker.widget.menu_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.databinding.BottomSheetMenuIconWithHeadlineBinding
import com.example.containertracker.utils.RecyclerViewItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class MenuIconWithHeadlineBottomSheet<T> : BaseBottomSheet() {

    private var _binding: BottomSheetMenuIconWithHeadlineBinding? = null
    protected val binding get() = _binding!!

    var adapter: MenuIconWithHeadlineAdapter<T>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMenuIconWithHeadlineBinding.inflate(inflater)

        lifecycleScope.launch {
            delay(1000)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()
    }

    private fun initEvent() = with(binding) {
        btnClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun initView() = with(binding){
        tvTitle.text = arguments?.getString(TITLE)

        menuList.adapter = adapter
        menuList.layoutManager = LinearLayoutManager(requireContext())
        menuList.addItemDecoration(RecyclerViewItemDecoration())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    companion object {
        const val TITLE = "title"

        fun <T> newInstance(
            title: String
        ) : MenuIconWithHeadlineBottomSheet<T> {
            val dialog = MenuIconWithHeadlineBottomSheet<T>()
            dialog.arguments = Bundle().apply {
                putString(TITLE, title)
            }
            return dialog
       }
    }
}