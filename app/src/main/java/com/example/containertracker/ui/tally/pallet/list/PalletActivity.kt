package com.example.containertracker.ui.tally.pallet.list

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.ActivityPalletBinding
import com.example.containertracker.databinding.FinishProcessTallyDialogBinding
import com.example.containertracker.databinding.LogoutDialogBinding
import com.example.containertracker.ui.tally.pallet.formpallet.PalletFormBottomSheet
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.constants.ResultCode
import org.koin.androidx.viewmodel.ext.android.viewModel

class PalletActivity : BaseActivity() {
    private lateinit var binding: ActivityPalletBinding
    private val viewModel: PalletViewModel by viewModel()
    private val palletAdapter by lazy {
        PalletAdapter(emptyList(), ::onEditClick, ::onDeleteClick)
    }

    private val tallySheet by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA, TallySheetDrafts::class.java)
        } else {
            intent.getParcelableExtra(ExtrasConstant.EXTRA_TALLY_SHEET_DATA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        initView()
        observer()
    }

    private fun observer() = viewModel.apply {
        palletsData.observe(this@PalletActivity, ::updateList)
        onDeleted.observeAction(this@PalletActivity, ::handleSuccessDeleted)
        onFinished.observeAction(this@PalletActivity) {
            handleFinish()
        }
    }

    private fun updateList(list: List<PalletResponse>?) {
        palletAdapter.items = list.orEmpty()
        binding.emptyStateData.isVisible = list.isNullOrEmpty()
        binding.divider.isVisible = !list.isNullOrEmpty()
        binding.buttonFinish.isVisible = !list.isNullOrEmpty()
    }

    private fun initView() = with(binding) {
        supportActionBar?.title = getString(R.string.pallet_list_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerPallet.adapter = palletAdapter
        recyclerPallet.layoutManager = LinearLayoutManager(
            this@PalletActivity,
            LinearLayoutManager.VERTICAL,
            false
        )

        buttonAdd.setOnClickListener { showPalletForm() }
        buttonFinish.setOnClickListener { showFinishProcessDialog() }

        viewModel.getPallets(tallySheet?.idTallySheet.orEmpty())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onEditClick(data: PalletResponse) {
        showPalletForm(data)
    }

    private fun onDeleteClick(data: PalletResponse) {
        showDeleteDialog(data.idTallySheetPallet)
    }

    private fun handleSuccessDeleted() {
        showSuccessMessage(getString(R.string.pallet_list_success_deleted_message))
        viewModel.getPallets(tallySheet?.idTallySheet.orEmpty())
    }

    private fun showPalletForm(data: PalletResponse?  = null) {
        PalletFormBottomSheet.newInstance(
            palletData = data,
            tallySheetData = tallySheet
        ).apply {
            onSubmit = {
                viewModel.getPallets(tallySheet?.idTallySheet.orEmpty())
            }
        }.also {
            it.show(supportFragmentManager, "tag")
        }
    }

    private fun showDeleteDialog(idTallySheetPallet: String?) {
        val dialog = Dialog(this)

        val bindingToast: LogoutDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.logout_dialog, null, false
        )

        bindingToast.lifecycleOwner = this

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.titleError.text = getString(R.string.pallet_list_delete_title)
        bindingToast.subtitleError.text = getString(R.string.pallet_list_delete_subtitle)
        bindingToast.btnNo.text = getString(R.string.general_action_cancel)
        bindingToast.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        bindingToast.btnYes.text = getString(R.string.general_action_delete)
        bindingToast.btnYes.setOnClickListener {
            viewModel.deletePallet(idTallySheetPallet.orEmpty())
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showFinishProcessDialog() {
        val dialog = Dialog(this)

        val bindingToast: FinishProcessTallyDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.finish_process_tally_dialog, null, false
        )

        bindingToast.lifecycleOwner = this

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(bindingToast.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingToast.btnFinish.setOnClickListener {
            viewModel.finishProcess(tallySheet?.idTallySheet.orEmpty())
        }
        bindingToast.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun handleFinish() {
        val resultIntent = Intent()
        setResult(ResultCode.RESULT_SUCCESS_SUBMIT, resultIntent)
        finish()
    }
}