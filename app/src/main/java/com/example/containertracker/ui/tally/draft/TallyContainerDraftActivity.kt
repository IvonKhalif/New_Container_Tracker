package com.example.containertracker.ui.tally.draft

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.R
import com.example.containertracker.base.BaseActivity
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.databinding.ActivityTallyContainerDraftBinding
import com.example.containertracker.databinding.LogoutDialogBinding
import com.example.containertracker.ui.tally.form.TallyFormActivity
import com.example.containertracker.utils.constants.ExtrasConstant.EXTRA_TALLY_SHEET_DATA
import org.koin.androidx.viewmodel.ext.android.viewModel

class TallyContainerDraftActivity : BaseActivity() {

    private lateinit var binding: ActivityTallyContainerDraftBinding
    private val viewModel: TallyContainerDraftViewModel by viewModel()
    private val containerAdapter by lazy {
        TallyContainerDraftAdapter(emptyList(), ::onItemClick, ::onDeleteClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTallyContainerDraftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initObserver()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initObserver() = viewModel.apply {
        draftsLiveData.observe(this@TallyContainerDraftActivity, ::updateList)
        onDeleted.observeAction(this@TallyContainerDraftActivity, ::handleSuccessDeleted)
    }

    private fun updateList(list: List<TallySheetDrafts>?) {
        containerAdapter.items = list.orEmpty()
        binding.containerEmptyStateData.isVisible = list.orEmpty().isEmpty()
    }

    private fun handleSuccessDeleted() {
        showSuccessMessage(getString(R.string.tally_drafts_success_deleted_message))
        viewModel.getDrafts()
    }

    private fun initView() = with(binding) {
        supportActionBar?.title = getString(R.string.tally_sheet_draft_list_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerContainer.adapter = containerAdapter
        recyclerContainer.layoutManager = LinearLayoutManager(
            this@TallyContainerDraftActivity,
            LinearLayoutManager.VERTICAL,
            false
        )

        viewModel.getDrafts()
    }

    private fun onItemClick(tallySheetDrafts: TallySheetDrafts) {
        val intent = Intent(this, TallyFormActivity::class.java)
        intent.putExtra(EXTRA_TALLY_SHEET_DATA, tallySheetDrafts)
        startActivity(intent)
    }

    private fun onDeleteClick(tallySheetDrafts: TallySheetDrafts) {
        showDeleteDialog(tallySheetDrafts.idTallySheet)
    }

    private fun showDeleteDialog(idTallySheet: String?) {
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

        bindingToast.titleError.text = getString(R.string.tally_drafts_delete_title)
        bindingToast.subtitleError.text = getString(R.string.tally_drafts_delete_subtitle)
        bindingToast.btnNo.text = getString(R.string.general_action_cancel)
        bindingToast.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        bindingToast.btnYes.text = getString(R.string.general_action_delete)
        bindingToast.btnYes.setOnClickListener {
            viewModel.deleteContainer(idTallySheet.orEmpty())
            dialog.dismiss()
        }

        dialog.show()
    }
}