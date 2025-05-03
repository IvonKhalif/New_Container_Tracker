package com.example.containertracker.ui.container.detail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.databinding.ActivityContainerDetailBinding
import com.example.containertracker.utils.printer.DeviceConnFactoryManager
import com.example.containertracker.utils.printer.DeviceConnFactoryManager.CONN_STATE_FAILED
import com.example.containertracker.utils.printer.PrinterUiModel
import com.example.containertracker.ui.container.printer_list.PrinterListBottomSheet
import com.example.containertracker.ui.flexi.form.FlexiFormParam
import com.example.containertracker.utils.constants.ExtrasConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContainerDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityContainerDetailBinding
    private val viewModel: ContainerDetailViewModel by viewModel()

    private val container by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_CONTAINER_DATA, ContainerDetail::class.java)
        } else {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_CONTAINER_DATA)
        }
    }

    private var deviceFactory: DeviceConnFactoryManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        observer()
    }

    private fun initView() {
        supportActionBar?.title = getString(R.string.container_qr_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel.getContainerQR(container?.id.orEmpty())

        binding.codeContainer = container?.codeContainer.orEmpty()
    }

    private fun initEvent() {
        binding.buttonPrint.setOnClickListener {
            showPrinterList()
        }
    }

    private fun observer() {
        viewModel.qrContainerLiveData.observe(this, ::handleImage)

        viewModel.printData.observe(this) {
            deviceFactory?.sendDataImmediately(it.command)
            handleLoadingWidget(false)
        }

        viewModel.containerImageBitmap.observe(this) { bitmap ->
            binding.buttonPrint.isEnabled = bitmap != null
        }
    }

    private fun handleImage(containerQR: ContainerDetail) {
        val base64 = containerQR.imgBase64.orEmpty()
        val imageByteArray: ByteArray = Base64.decode(base64, Base64.NO_WRAP)

        Glide.with(this)
            .asBitmap()
            .load(imageByteArray)
            .listener(object : RequestListener<Bitmap> { // copy bitmap to view-model to prepare print
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewModel.setBitmap(resource)
                    return false
                }
            }).into(binding.imageContainerQr)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE)
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    /**
     * show printer list bottom sheet
     */
    private fun showPrinterList() {
        val sheet = PrinterListBottomSheet.newInstance()
        // printer selected callback
        sheet.onPrinterSelected = {
            handleLoadingWidget(true)
            connect(it)
        }

        sheet.showNow(supportFragmentManager, sheet.javaClass.canonicalName)
    }

    /**
     * connect to printer
     */
    private fun connect(printer: PrinterUiModel) {
        // reset port
        deviceFactory?.closeAllPort()

        // create new instance factory
        deviceFactory = DeviceConnFactoryManager.Build()
            .setContext(this)
            .setMacAddress(printer.address)
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            // open port printer via mac address
            deviceFactory?.openPort()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * receive printer connection state
     */
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DeviceConnFactoryManager.ACTION_CONN_STATE == action) {
                when (intent.getIntExtra(DeviceConnFactoryManager.STATE, -1)) {
                    DeviceConnFactoryManager.CONN_STATE_CONNECTING -> {
                        // show toast connecting
                        showSuccessMessage(getString(R.string.printer_connecting))
                    }
                    DeviceConnFactoryManager.CONN_STATE_CONNECTED -> {
                        // show toast and do print
                        showSuccessMessage(getString(R.string.printer_connected))
                        viewModel.print()
                    }
                    CONN_STATE_FAILED -> {
                        // connection failed and clear all memory and view
                        deviceFactory?.closeAllPort()
                        handleLoadingWidget(false)
                        showErrorMessage(getString(R.string.printer_connection_failed))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deviceFactory?.closeAllPort()
        deviceFactory = null
    }
}