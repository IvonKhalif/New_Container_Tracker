package com.example.containertracker.ui.isotank.form

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.containertracker.R
import com.example.containertracker.base.BaseBottomSheet
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.databinding.BottomSheetIsoTankFormBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class IsoTankFormBottomSheet : BaseBottomSheet() {

    private lateinit var binding: BottomSheetIsoTankFormBinding
    private val viewModel: IsoTankFormViewModel by viewModel()

    // get container data
    private val container by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                IsoTankFormBottomSheet.ARG_CONTAINER_DATA,
                Container::class.java
            )
        } else {
            arguments?.getParcelable<Container>(IsoTankFormBottomSheet.ARG_CONTAINER_DATA)
        }
    }

    // callback to submit
    var onDone: () -> Unit = {}
    var onClose: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_iso_tank_form,
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

    private fun mainView() = with(binding) {
        // setup behavior bottom sheet
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.behavior.skipCollapsed = true
                sheet.parent.requestLayout()
            }
        }

        btnClose.setOnClickListener {
            onClose.invoke()
            dismissAllowingStateLoss()
        }

        buttonSubmit.setOnClickListener {
            viewModel.onSubmit()
        }

        viewModel.onSetupData(container)
    }

    private fun observer() {
        this.binding.viewModelBinding = viewModel.also {
            it.loadingState.observe(viewLifecycleOwner) { isLoading ->
                handleLoadingWidget(isLoading)
            }
            it.networkErrorState.observe(viewLifecycleOwner) { error ->
                showErrorMessage(error)
            }
            it.serverErrorState.observe(viewLifecycleOwner) { error ->
                showErrorMessage(error.status.orEmpty())
            }
            it.isManHoleOKChecked.observe(viewLifecycleOwner) {
                viewModel.onStatusManHoleOKCheck()
            }
            it.isManHoleNotOKChecked.observe(viewLifecycleOwner) {
                viewModel.onStatusManHoleNotOKCheck()
            }
            it.isManifoldOKChecked.observe(viewLifecycleOwner) {
                viewModel.onStatusManifoldOKCheck()
            }
            it.isManifoldNotOKChecked.observe(viewLifecycleOwner) {
                viewModel.onStatusManifoldNotOKCheck()
            }
            it.isInnerTankOKChecked.observe(viewLifecycleOwner) {
                viewModel.onStatusInnerTankOKCheck()
            }
            it.isInnerTankNotOKChecked.observe(viewLifecycleOwner) {
                viewModel.onStatusInnerTankNotOKCheck()
            }
            it.onSuccessSubmit.observeAction(viewLifecycleOwner) {
                onDone.invoke()
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        private const val ARG_CONTAINER_DATA = "container_data"

        fun newInstance(
            container: Container
        ): IsoTankFormBottomSheet {
            val sheet = IsoTankFormBottomSheet()

            sheet.arguments = Bundle().apply {
                putParcelable(ARG_CONTAINER_DATA, container)
            }

            return sheet
        }
    }
}