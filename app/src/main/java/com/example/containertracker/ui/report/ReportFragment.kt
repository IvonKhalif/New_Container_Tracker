package com.example.containertracker.ui.report

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.user.models.User
import com.example.containertracker.databinding.FragmentReportBinding
import com.example.containertracker.ui.scanner.ScannerActivity
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.URLUtil.createURLReport
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.widget.DatePickerWidget
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.format.DateTimeFormatter

class ReportFragment : BaseFragment() {

    private val viewModel: ReportViewModel by viewModel()
    private var _binding: FragmentReportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        binding.reportViewModel = viewModel
        binding.lifecycleOwner = this
        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.apply {
            startDate.setTitle("From Date")
            startDate.onClickDropDownListener {
                val datePicker = DatePickerWidget()
                datePicker.bind(viewModel.startDate)
                datePicker.show(childFragmentManager, datePicker.tag)
            }
            endDate.setTitle("To Date")
            endDate.onClickDropDownListener {
                val datePicker = DatePickerWidget()
                datePicker.bind(viewModel.endDate)
                datePicker.show(childFragmentManager, datePicker.tag)
            }
            searchWidgetReport.onBarcodeListener {
                resultLauncher.launch(Intent(baseActivity, ScannerActivity::class.java))
            }
            buttonSubmitReport.setOnClickListener {
                getURLReport()
            }
        }

        observer()
    }

    private fun observer() {
        viewModel.startDateString.observe(viewLifecycleOwner, ::onFromDateSelected)
        viewModel.endDateString.observe(viewLifecycleOwner, ::onToDateSelected)
        viewModel.genericErrorLiveData.observeNonNull(viewLifecycleOwner, ::handleErrorWidget)
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse) {
        showErrorMessage(genericErrorResponse.message.toString())
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

    private fun getURLReport() {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        val userId = user?.id.orEmpty()
        val startDate = viewModel.startDate.value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val endDate = viewModel.endDate.value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val qrCode = viewModel.containerData.value?.uniqueId.orEmpty()
        val containerCode = viewModel.keywordSearch.value.orEmpty()
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(createURLReport(
                    userId,
                    startDate.orEmpty(),
                    endDate.orEmpty(),
                    qrCode,
                    containerCode
                ))
            )
        )
    }

    override fun onResultData(result: Intent?) {
        super.onResultData(result)
        val container = result?.getParcelableExtra<Container>(ExtrasConstant.EXTRA_CONTAINER_DATA)
        viewModel.containerData.value = container
        viewModel.keywordSearch.value = container?.codeContainer.orEmpty()
    }
}