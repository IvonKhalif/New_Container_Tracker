package com.example.containertracker.ui.container

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.containertracker.base.BaseFragment
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.databinding.FragmentContainerBinding
import com.example.containertracker.ui.container.detail.ContainerDetailActivity
import com.example.containertracker.ui.history.detail.HistoryDetailActivity
import com.example.containertracker.utils.constants.ExtrasConstant
import com.example.containertracker.utils.extension.observeNonNull
import com.example.containertracker.utils.response.GenericErrorResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContainerFragment : BaseFragment() {

    private lateinit var binding: FragmentContainerBinding
    private val viewModel: ContainerViewModel by viewModel<ContainerViewModel>()
    private val containerAdapter by lazy {
        ContainerAdapter(emptyList(), ::onItemClick)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContainerBinding.inflate(inflater, container, false)
        binding.containerViewModel = viewModel
        binding.lifecycleOwner = this

        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.recyclerContainer.adapter = containerAdapter
        binding.recyclerContainer.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.buttonSubmitContainer.setOnClickListener {
            viewModel.getContainers()
        }
        observer()
    }

    private fun observer() {
        viewModel.apply {
            containerListLiveData.observe(viewLifecycleOwner, ::updateHistory)
            loadingWidgetLiveData.observeNonNull(viewLifecycleOwner, { handleLoadingWidget(it) })
            genericErrorLiveData.observeNonNull(viewLifecycleOwner, ::handleErrorWidget)
        }
    }

    private fun updateHistory(list: List<ContainerDetail>) {
        containerAdapter.items = list
        binding.containerEmptyStateData.isVisible = list.isEmpty()
    }

    private fun handleErrorWidget(genericErrorResponse: GenericErrorResponse) {
        binding.containerEmptyStateData.isVisible = true
        showErrorMessage(genericErrorResponse.status.toString())
    }

    fun onItemClick(container: ContainerDetail) {
        goToContainerDetailQR(container)
    }

    private fun goToContainerDetailQR(container: ContainerDetail) {
        val intent = Intent(activity, ContainerDetailActivity::class.java)
        intent.putExtra(ExtrasConstant.EXTRA_CONTAINER_DATA, container)
        startActivity(intent)
    }
}

