package com.example.containertracker.ui.selectlocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.R
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.databinding.BottomSheetSelectLocationBinding
import com.example.containertracker.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLocationBottomSheet(
    private val isContainerLaden: Boolean = false,
    private val onItemClick: (Location, List<Location>?) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSelectLocationBinding
    private val viewModel: SelectLocationViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val postAdapter by lazy {
        SelectLocationAdapter(mainViewModel.locationListLiveData.value.orEmpty(), ::onItemClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_select_location, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDataLocations(isContainerLaden)
        initView()
        observer()
    }

    private fun observer() {
        viewModel.apply {
            locationsLiveData.observe(viewLifecycleOwner, ::handleLocationList)
        }
    }

    private fun handleLocationList(list: List<Location>) {
        postAdapter.items = list
    }

    private fun initView() {
        binding.recyclerSelectPost.adapter = postAdapter
        binding.recyclerSelectPost.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun onItemClicked(item: Location) {
        onItemClick(item, viewModel.locationsLiveData.value)
        dismiss()
    }
}