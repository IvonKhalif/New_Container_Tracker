package com.example.containertracker.ui.container.printer_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.databinding.BottomSheetPrinterListBinding
import com.example.containertracker.utils.printer.PrinterPreparation
import com.example.containertracker.utils.printer.PrinterDiscovery
import com.example.containertracker.utils.printer.PrinterUiModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PrinterListBottomSheet : BottomSheetDialogFragment() {
    // view binding nullable
    private var _binding: BottomSheetPrinterListBinding? = null
    private val binding get() = _binding!!

    // print preparation
    private val printerPreparation by lazy {
        PrinterPreparation(requireContext())
    }
    // printer discovery service
    private lateinit var printerDiscovery: PrinterDiscovery

    // printer selected
    var onPrinterSelected: (PrinterUiModel) -> Unit = {}

    // printer available adapter
    private val adapter by lazy {
        PrinterListAdapter {
            onPrinterSelected.invoke(it)
            dismissAllowingStateLoss()
        }
    }

    // permission launcher for print preparation
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        printerPreparation.onPermissionForResult(permissions = permissions)
    }

    // setting launcher for print preparation
    private val settingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> printerPreparation.onSettingForResult(result = result) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        printerDiscovery = PrinterDiscovery(requireActivity())
        printerDiscovery.registerDiscovery()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPrinterListBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {
        with (binding) {
            recyclerSelectCondition.adapter = adapter
            recyclerSelectCondition.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        initEvent()
    }

    /**
     * init event on view
     */
    private fun initEvent() {
        printerPreparation.start()

        binding.btnClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun observer() {
        printerPreparation.onNeedPermission = { permissions ->
            permissionLauncher.launch(permissions)
        }

        printerPreparation.onOpenSetting = {
            settingLauncher.launch(it)
        }

        printerPreparation.onPrinterReady = {
            printerDiscovery.startDiscovery()
        }

        printerDiscovery.onDiscoveryFinished = {
            binding.progressBar.isGone = true
        }

        printerDiscovery.onDeviceAvailable = { devices ->
            binding.recyclerSelectCondition.post {
                adapter.submitList(devices)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        printerDiscovery.stopDiscovery()
        _binding = null
    }

    companion object {
        fun newInstance() = PrinterListBottomSheet()
    }
}