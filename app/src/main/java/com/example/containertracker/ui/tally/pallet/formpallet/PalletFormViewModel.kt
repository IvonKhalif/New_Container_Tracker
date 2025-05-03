package com.example.containertracker.ui.tally.pallet.formpallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.data.tally.request.SavePalletRequest
import com.example.containertracker.data.tally.request.UpdatePalletRequest
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.usecase.GetPalletDetailUseCase
import com.example.containertracker.domain.tally.usecase.SavePalletUseCase
import com.example.containertracker.domain.tally.usecase.UpdatePalletUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch

class PalletFormViewModel(
    private val savePalletUseCase: SavePalletUseCase,
    private val updatePalletUseCase: UpdatePalletUseCase,
    private val getPalletDetailUseCase: GetPalletDetailUseCase
): BaseViewModel() {
    val palletIdLiveData = MutableLiveData("")
    val batchNumberLiveData = MutableLiveData("")
    val quantityLiveData = MutableLiveData("")
    val rejectLiveData = MutableLiveData("")
    val loadedLiveData = MutableLiveData("")
    val actualBatchLiveData = MutableLiveData("")
    val remarksLiveData = MutableLiveData("")

    val palletDetail = MutableLiveData<PalletResponse>()

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun save(tallySheet: TallySheetDrafts?) = viewModelScope.launch{
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        showLoadingWidget()
        when(val response = savePalletUseCase(
            SavePalletRequest(
                accountId = user?.id.orEmpty(),
                idTallySheet = tallySheet?.idTallySheet,
                salesOrderDetailId = tallySheet?.idSalesOrderDetail,
                batchNumber = batchNumberLiveData.value,
                quantity = quantityLiveData.value?.toIntOrNull(),
                reject = rejectLiveData.value,
                loaded = loadedLiveData.value?.toIntOrNull(),
                actualBatch = actualBatchLiveData.value,
                remarks = remarksLiveData.value,
                palletId = palletIdLiveData.value
            )
        )) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onSuccessSubmit.postAction()
                } else {
                    _serverErrorState.value = GenericErrorResponse(
                        status = response.body.status,
                        message = response.body.toString()
                    )
                }
            }
            is NetworkResponse.ServerError -> _serverErrorState.value = response.body
            is NetworkResponse.NetworkError -> _networkErrorState.value = response.error
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
        hideLoadingWidget()
    }

    fun update(pallet: PalletResponse?) = viewModelScope.launch{
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        showLoadingWidget()
        when(val response = updatePalletUseCase(
            UpdatePalletRequest(
                accountId = user?.id,
                idTallySheet = pallet?.idTallySheet,
                salesOrderDetailId = pallet?.idSalesOrderDetail,
                batchNumber = batchNumberLiveData.value,
                quantity = quantityLiveData.value?.toIntOrNull(),
                reject = rejectLiveData.value,
                loaded = loadedLiveData.value?.toIntOrNull(),
                actualBatch = actualBatchLiveData.value,
                remarks = remarksLiveData.value,
                idTallySheetPallet = pallet?.idTallySheetPallet,
                palletId = pallet?.palletId
            )
        )) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onSuccessSubmit.postAction()
                } else {
                    _serverErrorState.value = GenericErrorResponse(
                        status = response.body.status,
                        message = response.body.toString()
                    )
                }
            }
            is NetworkResponse.ServerError -> {}
            is NetworkResponse.NetworkError -> _networkErrorState.value = response.error
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
        hideLoadingWidget()
    }

    fun getDetail(pallet: PalletResponse? = null) = viewModelScope.launch {
        showLoadingWidget()
        when (val response = getPalletDetailUseCase(pallet?.idTallySheetPallet.orEmpty())) {
            is NetworkResponse.Success -> {
                response.body.data.let { data ->
                    palletDetail.value = data
                    assignDataPallet(data)
                }
            }
            is NetworkResponse.ServerError -> {
                _serverErrorState.value = response.body
            }
            is NetworkResponse.NetworkError -> {
                _networkErrorState.value = response.error
            }
            is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
        }
        hideLoadingWidget()
    }

    private fun assignDataPallet(data: PalletResponse) {
        batchNumberLiveData.value = data.batchNumber
        quantityLiveData.value = data.quantity.toString()
        rejectLiveData.value = data.reject
        loadedLiveData.value = data.loaded.toString()
        actualBatchLiveData.value = data.actualBatch
        remarksLiveData.value = data.remarks
    }
}