package com.example.containertracker.ui.isotank.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.isotank.requests.SaveIsoTankRequest
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.isotank.usecase.SaveIsoTankUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.RemarkStatusEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IsoTankFormViewModel(
    private val saveIsoTankUseCase: SaveIsoTankUseCase
) : BaseViewModel() {
    var isoTankCap = MutableLiveData("")
    var isoTankTarra = MutableLiveData("")
    var isoTankRemark = MutableLiveData("")
    var isManHoleOKChecked = MutableLiveData<Boolean>(false)
    var isManHoleNotOKChecked = MutableLiveData<Boolean>(false)
    var isManifoldOKChecked = MutableLiveData<Boolean>(false)
    var isManifoldNotOKChecked = MutableLiveData<Boolean>(false)
    var isInnerTankOKChecked = MutableLiveData<Boolean>(false)
    var isInnerTankNotOKChecked = MutableLiveData<Boolean>(false)
    val isActionSubmitEnable = MutableLiveData<Boolean>(false)

    private val _containerData = MutableLiveData<Container>()
    val containerData: LiveData<Container> get() = _containerData

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun onSetupData(
        container: Container?
    ) = viewModelScope.launch {
        container?.let {
            _containerData.value = it
        }
    }

    private fun setButtonSubmitEnable() {
        isActionSubmitEnable.value = manHoleStatus().isNotBlank() && manifoldStatus().isNotBlank() && innerTankStatus().isNotBlank()
    }

    fun onStatusManHoleOKCheck() {
        if (isManHoleOKChecked.value.orFalse()) isManHoleNotOKChecked.value = false
        setButtonSubmitEnable()
    }

    fun onStatusManHoleNotOKCheck() {
        if (isManHoleNotOKChecked.value.orFalse()) isManHoleOKChecked.value = false
        setButtonSubmitEnable()
    }

    fun onStatusManifoldOKCheck() {
        if (isManifoldOKChecked.value.orFalse()) isManifoldNotOKChecked.value = false
        setButtonSubmitEnable()
    }

    fun onStatusManifoldNotOKCheck() {
        if (isManifoldNotOKChecked.value.orFalse()) isManifoldOKChecked.value = false
        setButtonSubmitEnable()
    }

    fun onStatusInnerTankOKCheck() {
        if (isInnerTankOKChecked.value.orFalse()) isInnerTankNotOKChecked.value = false
        setButtonSubmitEnable()
    }

    fun onStatusInnerTankNotOKCheck() {
        if (isInnerTankNotOKChecked.value.orFalse()) isInnerTankOKChecked.value = false
        setButtonSubmitEnable()
    }

    private fun manHoleStatus(): String = when {
        isManHoleOKChecked.value.orFalse() && !isManHoleNotOKChecked.value.orFalse() -> RemarkStatusEnum.OK.status
        !isManHoleOKChecked.value.orFalse() && isManHoleNotOKChecked.value.orFalse() -> RemarkStatusEnum.NOT_OK.status
        else -> RemarkStatusEnum.EMPTY.status
    }

    private fun manifoldStatus(): String = when {
        isManifoldOKChecked.value.orFalse() && !isManifoldNotOKChecked.value.orFalse() -> RemarkStatusEnum.OK.status
        !isManifoldOKChecked.value.orFalse() && isManifoldNotOKChecked.value.orFalse() -> RemarkStatusEnum.NOT_OK.status
        else -> RemarkStatusEnum.EMPTY.status
    }

    private fun innerTankStatus(): String = when {
        isInnerTankOKChecked.value.orFalse() && !isInnerTankNotOKChecked.value.orFalse() -> RemarkStatusEnum.OK.status
        !isInnerTankOKChecked.value.orFalse() && isInnerTankNotOKChecked.value.orFalse() -> RemarkStatusEnum.NOT_OK.status
        else -> RemarkStatusEnum.EMPTY.status
    }

    fun onSubmit() = viewModelScope.launch {
        showLoadingWidget()

        val requestParam = withContext(Dispatchers.IO) {
            createSaveModel()
        }

        when (val response = saveIsoTankUseCase(requestParam)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    onSuccessSubmit.post()
                } else {
                    _serverErrorState.value = GenericErrorResponse(
                        status = response.body.status,
                        message = response.body.toString()
                    )
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

    private fun createSaveModel(): SaveIsoTankRequest {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)

        return SaveIsoTankRequest(
            userId = user?.id,
            salesOrderDetailId = containerData.value?.salesOrderDetailId,
            isoTankCap = isoTankCap.value,
            isoTankTarra = isoTankTarra.value,
            containerRemark = isoTankRemark.value,
            statusInnerTank = innerTankStatus(),
            statusManHole = manHoleStatus(),
            statusManifold = manifoldStatus(),
        )
    }
}