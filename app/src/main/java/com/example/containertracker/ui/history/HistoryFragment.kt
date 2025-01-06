package com.example.containertracker.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.databinding.FragmentHistoryBinding
import com.example.containertracker.ui.scanner.ScannerActivity
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_CONTAINER_DATA
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.widget.DatePickerWidget
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : BaseFragment() {

    private val viewModel: HistoryViewModel by viewModel()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyAdapter by lazy {
        HistoryAdapter(false, ::onHistoryClicked)
    }

    private fun onHistoryClicked(historyModel: HistoryModel) {
        //TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyViewModel = viewModel
        binding.lifecycleOwner = this
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.startDate.setTitle("From Date")
        binding.startDate.onClickDropDownListener {
            val datePicker = DatePickerWidget()
            datePicker.bind(viewModel.startDate)
            datePicker.show(childFragmentManager, datePicker.tag)
        }
        binding.endDate.setTitle("To Date")
        binding.endDate.onClickDropDownListener {
            val datePicker = DatePickerWidget()
            datePicker.bind(viewModel.endDate)
            datePicker.show(childFragmentManager, datePicker.tag)
        }

        binding.searchWidgetHistory.onBarcodeListener {
            resultLauncher.launch(Intent(baseActivity, ScannerActivity::class.java))
        }

        binding.recyclerHistory.adapter = historyAdapter
        binding.recyclerHistory.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.buttonSubmitHistory.setOnClickListener {
            viewModel.getHistory()
        }

        observer()
    }

    private fun observer() {
        viewModel.apply {
            startDateDisplay.observe(viewLifecycleOwner, ::onFromDateSelected)
            endDateDisplay.observe(viewLifecycleOwner, ::onToDateSelected)
            historyListLiveData.observeNonNull(viewLifecycleOwner, ::updateHistory)
            loadingWidgetLiveData.observeNonNull(viewLifecycleOwner) { handleLoadingWidget(it) }
            genericErrorLiveData.observeNonNull(viewLifecycleOwner, ::handleErrorWidget)
        }
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse) {
//        if (genericErrorResponse.status == NetworkUtil.REQUEST_NOT_FOUND)
//            binding.containerEmptyStateData.isVisible = true
//        else
        binding.containerEmptyStateData.isVisible = true
        showErrorMessage(genericErrorResponse.status.toString())
    }

    private fun updateHistory(list: List<HistoryModel>) {
        binding.recyclerHistory.post {
            historyAdapter.submitList(list)
        }
        binding.containerEmptyStateData.isVisible = list.isEmpty()
    }

    private fun onToDateSelected(dateString: String?) {
        dateString?.let { binding.endDate.setText(dateString) }
    }

    private fun onFromDateSelected(dateString: String?) {
        dateString?.let { binding.startDate.setText(dateString) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResultData(result: Intent?) {
        super.onResultData(result)
        val container = result?.getParcelableExtra<Container>(EXTRA_CONTAINER_DATA)
        viewModel.containerData.value = container
        viewModel.keywordSearch.value = container?.codeContainer.orEmpty()
    }
}