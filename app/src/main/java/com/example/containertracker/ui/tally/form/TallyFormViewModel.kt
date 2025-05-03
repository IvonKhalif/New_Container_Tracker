package com.example.containertracker.ui.tally.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.tally.usecase.GetTallyDetailUseCase
import com.example.containertracker.domain.tally.usecase.SaveTallySheetUseCase
import com.example.containertracker.utils.ActionLiveData
import com.example.containertracker.utils.DateUtil
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.ConditionEnum
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.enums.TallyConditionCheckedEnum
import com.example.containertracker.utils.enums.getContainerCondition
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class TallyFormViewModel(
    private val saveTallySheetUseCase: SaveTallySheetUseCase,
    private val getTallyDetailUseCase: GetTallyDetailUseCase
): BaseViewModel() {

    val tallyDetailLiveData = MutableLiveData<TallySheetDetailResponse>()
    val buyerLiveData = MutableLiveData("")
    val forkliftDriverLiveData = MutableLiveData("")
    val stufferLiveData = MutableLiveData("")
    val polyfoamLiveData = MutableLiveData("")
    val polywoodLiveData = MutableLiveData("")
    val cartoonSheet1LiveData = MutableLiveData("")
    val cartoonSheet2LiveData = MutableLiveData("")
    val palletIdLiveData = MutableLiveData("")
    val qtyPalletLiveData = MutableLiveData("")
    val qtyBagsLiveData = MutableLiveData("")
    val paperBagLiveData = MutableLiveData("")
    val jumboBagLiveData = MutableLiveData("")
    val stellDrumLiveData = MutableLiveData("")
    val hdpeDrumLiveData = MutableLiveData("")
    val ibcTankLiveData = MutableLiveData("")
    val totalLabelLiveData = MutableLiveData("")
    val stripingBeltLiveData = MutableLiveData("")
    var isLeftLockBarChecked = MutableLiveData<Boolean>(false)
    var isRightLockBarChecked = MutableLiveData<Boolean>(false)
    var isPlasticWrappedYesChecked = MutableLiveData<Boolean>(false)
    var isPlasticWrappedNoChecked = MutableLiveData<Boolean>(false)
    var isTaliChecked = MutableLiveData<Boolean>(false)
    var isStripingBeltChecked = MutableLiveData<Boolean>(false)
    var isStripingBentChecked = MutableLiveData<Boolean>(false)
    var isPalletizedChecked = MutableLiveData<Boolean>(false)
    var isUnPalletizedChecked = MutableLiveData<Boolean>(false)
    var isShipMarkChecked = MutableLiveData<Boolean>(false)
    var isGHSChecked = MutableLiveData<Boolean>(false)
    var isDGChecked = MutableLiveData<Boolean>(false)
    var isPSNChecked = MutableLiveData<Boolean>(false)
    var isNetralPaperBagChecked = MutableLiveData<Boolean>(false)
    var isSociPaperBagChecked = MutableLiveData<Boolean>(false)
    var isScplPaperBagChecked = MutableLiveData<Boolean>(false)
    var isBlueStellDrumChecked = MutableLiveData<Boolean>(false)
    var isGreyStellDrumChecked = MutableLiveData<Boolean>(false)
    var isGreenStellDrumChecked = MutableLiveData<Boolean>(false)

    var loadingDate = MutableLiveData(LocalDate.now())
    var loadingDateDisplay: LiveData<String> = loadingDate.map {
        when (it) {
            null -> ""
            LocalDate.now() -> "Today"
            else -> DateUtil.formatLocalDateToString(it, "dd MMMM yyyy")
        }
    }

    val startTimeLiveData = MutableLiveData("")
    val finishTimeLiveData = MutableLiveData("")

    private val _roofConditionData = MutableLiveData<ConditionEnum>()
    val roofConditionData: LiveData<ConditionEnum> get() = _roofConditionData

    private val _floorConditionData = MutableLiveData<ConditionEnum>()
    val floorConditionData: LiveData<ConditionEnum> get() = _floorConditionData

    private val _doorConditionData = MutableLiveData<ConditionEnum>()
    val doorConditionData: LiveData<ConditionEnum> get() = _doorConditionData

    private val _wallConditionData = MutableLiveData<ConditionEnum>()
    val wallConditionData: LiveData<ConditionEnum> get() = _wallConditionData

    // result when on-submit
    val onSuccessSubmit = ActionLiveData()

    fun save(
        container: TallySheetDrafts?,
        forkliftDriver: String?,
        stuffer: String?,
        palletQuantity: String?,
        bagsQuantity: String?,
        palletId: String?,
        stripingBelt: String?,
        polyfoam: String?,
        polywood: String?,
        cartoonSheet2x1: String?,
        cartoonSheet1x1: String?,
        paperBag: String?,
        jumboBag: String?,
        stellDrum: String?,
        hdpeDrum: String?,
        ibcTank: String?,
        totalLabel: String?,
    ) = viewModelScope.launch{
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        val tallyName = user?.name.orEmpty()
        val loadingDateString = loadingDate.value?.let { DateUtil.formatLocalDateToString(it, "yyyy-MM-dd") }

        showLoadingWidget()
        when(val response = saveTallySheetUseCase(
            container?.id,
            container?.idSalesOrderDetail,
            tallyName,
            buyerLiveData.value,
            forkliftDriver,
            stuffer,
            loadingDateString,
            startTimeLiveData.value,
            finishTimeLiveData.value,
            palletQuantity,
            bagsQuantity,
            palletId,
            roofConditionData.value?.type,
            floorConditionData.value?.type,
            wallConditionData.value?.type,
            doorConditionData.value?.type,
            isLeftLockBarChecked.value,
            isRightLockBarChecked.value,
            isPlasticWrappedYesChecked.value,
            isPlasticWrappedNoChecked.value,
            isTaliChecked.value,
            polyfoam,
            cartoonSheet1x1 = cartoonSheet1x1?.toIntOrNull(),
            cartoonSheet2x1 = cartoonSheet2x1?.toIntOrNull(),
            stripingBelt = stripingBelt?.toIntOrNull(),
            isStripingBentChecked.value,
            polywood,
            paperBag,
            isNetralPaperBag = isNetralPaperBagChecked.value,
            isSociPaperBag = isSociPaperBagChecked.value,
            isScplPaperBag = isScplPaperBagChecked.value,
            jumboBag,
            stellDrum,
            isBlueStellDrum = isBlueStellDrumChecked.value,
            isGreyStellDrum = isGreyStellDrumChecked.value,
            isGreenStellDrum = isGreenStellDrumChecked.value,
            hdpeDrum,
            ibcTank,
            isPalletizedChecked.value,
            isUnPalletizedChecked.value,
            isShipMarkChecked.value,
            isGHSChecked.value,
            isDGChecked.value,
            isPSNChecked.value,
            totalLabel
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

    fun getDetail(idTally: String = "", isShowLoading: Boolean = true) = viewModelScope.launch {
        if (isShowLoading) showLoadingWidget()
        when (val response = getTallyDetailUseCase(idTally)) {
            is NetworkResponse.Success -> {
                response.body.data.let { data ->
                    tallyDetailLiveData.value = data
                    assignDataTally(data)
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
        if (isShowLoading) hideLoadingWidget()
    }

    private fun assignDataTally(data: TallySheetDetailResponse) {
        val loadingLocalDate = if (data.loadingDate.orEmpty().contains("0000").orFalse()) LocalDate.now() else LocalDate.parse(data.loadingDate)
        buyerLiveData.value = data.buyer
        forkliftDriverLiveData.value = data.driverForklift
        stufferLiveData.value = data.stuffer
        loadingDate.value = loadingLocalDate
        startTimeLiveData.value = data.startTime
        finishTimeLiveData.value = data.finishTime
        qtyPalletLiveData.value = data.quantityPallet
        qtyBagsLiveData.value = data.quantityBags
        palletIdLiveData.value = data.idPallet
        _roofConditionData.value = data.roofCondition.getContainerCondition()
        _floorConditionData.value = data.floorCondition.getContainerCondition()
        _wallConditionData.value = data.wallCondition.getContainerCondition()
        _doorConditionData.value = data.doorCondition.getContainerCondition()
        isLeftLockBarChecked.value = isConditionChecked(data.leftLockBar)
        isRightLockBarChecked.value = isConditionChecked(data.rightLockBar)
        isPlasticWrappedYesChecked.value = isPlasticWrappedYes(data.plasticWrapped)
        isPlasticWrappedNoChecked.value = isPlasticWrappedNo(data.plasticWrapped)
        isTaliChecked.value = isConditionChecked(data.tali)
        polyfoamLiveData.value = data.polyfoam.toString()
//        cartoonSheet1LiveData.value = data.cartoonSheet.toString()
        isStripingBeltChecked.value = isConditionChecked(data.stripingBelt)
        isStripingBentChecked.value = isConditionChecked(data.stripingBent)
        polywoodLiveData.value = data.polywood.toString()
        paperBagLiveData.value = data.paperBag.toString()
        jumboBagLiveData.value = data.jumboBag.toString()
        stellDrumLiveData.value = data.stellDrum.toString()
        hdpeDrumLiveData.value = data.hdpeDrum.toString()
        ibcTankLiveData.value = data.ibcTank.toString()
        isPalletizedChecked.value = isConditionChecked(data.palletized)
        isUnPalletizedChecked.value  = isConditionChecked(data.unpalletized)
        isShipMarkChecked.value  = isConditionChecked(data.shipMark)
        isGHSChecked.value  = isConditionChecked(data.ghs)
        isDGChecked.value  = isConditionChecked(data.dg)
        isPSNChecked.value = isConditionChecked(data.psn)
        totalLabelLiveData.value = data.totalLabel.toString()
    }

     fun isPlasticWrappedYes(isYesChecked: String?) =
        isYesChecked == TallyConditionCheckedEnum.YES.desc

     fun isPlasticWrappedNo(isNoChecked: String?) =
        isNoChecked == TallyConditionCheckedEnum.NO.desc

     fun isConditionChecked(checked: String?) =
        checked.orEmpty() == TallyConditionCheckedEnum.Y.desc

    // on select condition change each container side
    fun onSelectConditionChanged(
        side: ContainerSidesEnum,
        conditionEnum: ConditionEnum
    ) = viewModelScope.launch {

        // load item in container images recyclerview
//        loadContainerImageItem(side = side, condition = conditionEnum)

        // assign to properties
        when (side) {
            ContainerSidesEnum.DOOR -> _doorConditionData.value = conditionEnum
            ContainerSidesEnum.WALL -> _wallConditionData.value = conditionEnum
            ContainerSidesEnum.ROOF -> _roofConditionData.value = conditionEnum
            ContainerSidesEnum.FLOOR -> _floorConditionData.value = conditionEnum
            else -> {}
        }
    }
}