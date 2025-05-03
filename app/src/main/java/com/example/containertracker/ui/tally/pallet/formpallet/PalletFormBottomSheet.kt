package com.example.containertracker.ui.tally.pallet.formpallet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.containertracker.R
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.BottomSheetPalletFormBinding
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_TALLY_SHEET_DATA
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class PalletFormBottomSheet : BaseBottomSheet() {
    private lateinit var binding: BottomSheetPalletFormBinding
    private val viewModel: PalletFormViewModel by viewModel()

    private val tallySheet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_TALLY_SHEET_DATA, TallySheetDrafts::class.java)
        } else {
            arguments?.getParcelable(EXTRA_TALLY_SHEET_DATA)
        }
    }

    private val pallet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_PALLET_DATA, PalletResponse::class.java)
        } else {
            arguments?.getParcelable(EXTRA_PALLET_DATA)
        }
    }

    var onSubmit: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_pallet_form,
            container,
            false
        )

        binding.viewModelBinding = viewModel
        binding.lifecycleOwner = parentFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainView()
        observer()
    }

    private fun observer() = viewModel.apply {
        loadingState.observe(viewLifecycleOwner) {
            handleLoadingWidget(it)
        }
        networkErrorState.observe(viewLifecycleOwner) {
            showErrorMessage(it)
        }
        serverErrorState.observe(viewLifecycleOwner) {
            showErrorMessage(it.status.orEmpty())
        }
        palletDetail.observe(this@PalletFormBottomSheet, ::assignPalletValue)
        onSuccessSubmit.observeAction(viewLifecycleOwner) {
            dismissAllowingStateLoss()
            onSubmit()
        }
    }


    private fun mainView() = with(binding) {
        // setup behavior bottom sheet
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.behavior.skipCollapsed = true
                sheet.parent.requestLayout()
            }
        }

        btnClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
        buttonSubmit.setOnClickListener {
            if (pallet == null) {
                viewModel.save(tallySheet)
            } else {
                viewModel.update(pallet)
            }
        }

        if (pallet != null) {
            viewModel.getDetail(pallet)
        }
    }

    private fun assignPalletValue(data: PalletResponse) = binding.apply {
        inputBatchNumber.setTextInput(data.batchNumber)
        inputPalletId.setTextInput(data.palletId)
        inputPalletQty.setTextInput(data.quantity.toString())
        inputReject.setTextInput(data.reject)
        inputLoaded.setTextInput(data.loaded.toString())
        inputActualBatch.setTextInput(data.actualBatch)
        inputRemarks.setTextInput(data.remarks)
    }

    companion object {
        private const val EXTRA_PALLET_DATA = "EXTRA_PALLET_DATA"
        fun newInstance(palletData: PalletResponse?, tallySheetData: TallySheetDrafts?) =
            PalletFormBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_TALLY_SHEET_DATA, tallySheetData)
                    putParcelable(EXTRA_PALLET_DATA, palletData)
                }
            }
    }
}