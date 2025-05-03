package com.example.containertracker.ui.home.containercondition

import android.app.Activity
import android.content.Intent
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.container.requests.SaveContainerHistoryRequest
import com.example.containertracker.data.localsales.requests.SaveLocalSalesRequest
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.data.user.models.User
import com.example.containertracker.domain.container.SaveHistoryUseCase
import com.example.containertracker.domain.localsales.usecase.SaveLocalSalesContainerUseCase
import com.example.containertracker.ui.home.containercondition.models.ContainerConditionParam
import com.example.containertracker.ui.home.containercondition.models.ContainerImageUiModel
import com.example.containertracker.ui.home.containerdefect.ContainerDefectParam
import com.example.containertracker.ui.media.image_preview.ImagePreviewActivity
import com.example.containertracker.utils.DateUtil
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.enums.ConditionEnum
import com.example.containertracker.utils.enums.ContainerRejectionStatusEnum
import com.example.containertracker.utils.enums.ContainerSidesEnum
import com.example.containertracker.utils.enums.StatusResponseEnum
import com.example.containertracker.utils.enums.TypeScanEnum
import com.example.containertracker.utils.enums.getContainerCondition
import com.example.containertracker.utils.enums.isGood
import com.example.containertracker.utils.response.GenericErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.io.File

class ContainerConditionViewModel(
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val saveLocalSalesContainerUseCase: SaveLocalSalesContainerUseCase
) : BaseViewModel() {

    private val _containerData = MutableLiveData<Container>()
    val containerData: LiveData<Container> get() = _containerData

    private val _locationData = MutableLiveData<Location>()

    private val _voyageIn = MutableLiveData<String>()

    private val _voyageOut = MutableLiveData<String>()
    // data binding
    val tarraWeight = MutableLiveData<String>()
    val sealNumberData = MutableLiveData<String>()
    val rejectionStatus = MutableLiveData<ContainerRejectionStatusEnum>()
    var containerDescription = MutableLiveData("")

    private val _salesOrderNumberData = MutableLiveData<String>()
    val salesOrderNumberData: LiveData<String> get() = _salesOrderNumberData

    private val _upperConditionData = MutableLiveData<ConditionEnum>()
    val upperConditionData: LiveData<ConditionEnum> get() = _upperConditionData

    private val _floorConditionData = MutableLiveData<ConditionEnum>()
    val floorConditionData: LiveData<ConditionEnum> get() = _floorConditionData

    private val _doorConditionData = MutableLiveData<ConditionEnum>()
    val doorConditionData: LiveData<ConditionEnum> get() = _doorConditionData

    private val _backConditionData = MutableLiveData<ConditionEnum>()
    val backConditionData: LiveData<ConditionEnum> get() = _backConditionData

    private val _leftConditionData = MutableLiveData<ConditionEnum>()
    val leftConditionData: LiveData<ConditionEnum> get() = _leftConditionData

    private val _rightConditionData = MutableLiveData<ConditionEnum>()
    val rightConditionData: LiveData<ConditionEnum> get() = _rightConditionData

    private val _openMediaState = MutableLiveData<ContainerImageUiModel>()
    val openMediaState: LiveData<ContainerImageUiModel> get() = _openMediaState

    private val _openImagePreviewState = MutableLiveData<ContainerImageUiModel>()
    val openImagePreviewState: LiveData<ContainerImageUiModel> get() = _openImagePreviewState

    val containerImageVisibilityState: LiveData<Boolean>
        get() = combine(
            upperConditionData.asFlow(),
            floorConditionData.asFlow(),
            doorConditionData.asFlow(),
            backConditionData.asFlow(),
            leftConditionData.asFlow(),
            rightConditionData.asFlow()
        ) { results -> results.any { true } }.asLiveData(Dispatchers.Main)

    private val _imageList = MutableLiveData<List<ContainerImageUiModel>>()
    val imageList: LiveData<List<ContainerImageUiModel>> get() = _imageList

    // result when on-submit
    private val _submitState = MutableLiveData<SaveContainerHistoryRequest>()
    val submitState: LiveData<SaveContainerHistoryRequest> get() = _submitState

    private val _submitLocalSalesState = MutableLiveData<SaveLocalSalesRequest>()
    val submitLocalSalesState: LiveData<SaveLocalSalesRequest> get() = _submitLocalSalesState

    //Container Defect Data
    var containerDefectParam: ContainerDefectParam? = null
    var manufactureDate = MutableLiveData(LocalDate.now())
    var manufactureDateDisplay: LiveData<String> = manufactureDate.map {
        when (it) {
            null -> ""
            LocalDate.now() -> "Today"
            else -> DateUtil.formatLocalDateToString(it, "dd MMM yyyy")
        }
    }

    private val _containerScanType = MutableLiveData<TypeScanEnum>()
    val containerScanType: LiveData<TypeScanEnum> get() = _containerScanType

    // on setup data when on view created
    fun onSetupData(param: ContainerConditionParam) = viewModelScope.launch {

        param.location?.let {
            _locationData.value = it
        }
        param.voyageIdIn?.let {
            _voyageIn.value = it
        }
        param.voyageIdOut?.let {
            _voyageOut.value = it
        }
        param.container.let {
            _containerData.value = it

            setPropertiesValue(container = it)
            setImageContainer()
        }
        param.containerLadenScanType?.let {
            _containerScanType.value = it
        }
        rejectionStatus.value = param.rejectionStatusEnum
        containerDefectParam = param.containerDefectParam
    }

    private fun setPropertiesValue(container: Container) {
        with(container) {
            sealNumberData.value = sealId.orEmpty()
            _salesOrderNumberData.value = salesOrderNumber.orEmpty()
            _backConditionData.value = backDoorSideCondition.getContainerCondition()
            _doorConditionData.value = frontDoorSideCondition.getContainerCondition()
            _upperConditionData.value = roofSideCondition.getContainerCondition()
            _floorConditionData.value = floorSideCondition.getContainerCondition()
            _leftConditionData.value = leftSideCondition.getContainerCondition()
            _rightConditionData.value = rightSideCondition.getContainerCondition()
        }
    }

    /**
     * prepare image container when first face
     */
    private fun setImageContainer() {
        val images = mutableListOf<ContainerImageUiModel>()

        addToImageContainerData(
            store = images,
            side = ContainerSidesEnum.BACK,
            condition = _backConditionData.value
        )

        addToImageContainerData(
            store = images,
            side = ContainerSidesEnum.DOOR,
            condition = _doorConditionData.value
        )

        addToImageContainerData(
            store = images,
            side = ContainerSidesEnum.ROOF,
            condition = _upperConditionData.value
        )

        addToImageContainerData(
            store = images,
            side = ContainerSidesEnum.FLOOR,
            condition = _floorConditionData.value
        )

        addToImageContainerData(
            store = images,
            side = ContainerSidesEnum.LEFT,
            condition = _leftConditionData.value
        )

        addToImageContainerData(
            store = images,
            side = ContainerSidesEnum.RIGHT,
            condition = _rightConditionData.value
        )

        _imageList.value = images
    }

    private fun addToImageContainerData(
        store: MutableList<ContainerImageUiModel>,
        side: ContainerSidesEnum,
        condition: ConditionEnum?
    ) {
        if (condition != null) {
            store.add(ContainerImageUiModel(position = side, condition = condition))
        }
    }

    // on select condition change each container side
    fun onSelectConditionChanged(
        side: ContainerSidesEnum,
        conditionEnum: ConditionEnum
    ) = viewModelScope.launch {

        // load item in container images recyclerview
        loadContainerImageItem(side = side, condition = conditionEnum)

        // assign to properties
        when (side) {
            ContainerSidesEnum.RIGHT -> _rightConditionData.value = conditionEnum
            ContainerSidesEnum.LEFT -> _leftConditionData.value = conditionEnum
            ContainerSidesEnum.DOOR -> _doorConditionData.value = conditionEnum
            ContainerSidesEnum.BACK -> _backConditionData.value = conditionEnum
            ContainerSidesEnum.ROOF -> _upperConditionData.value = conditionEnum
            ContainerSidesEnum.FLOOR -> _floorConditionData.value = conditionEnum
            else -> {}
        }
    }

    /**
     * prepare image container to recyclerview image list
     */
    private fun loadContainerImageItem(side: ContainerSidesEnum, condition: ConditionEnum) {
        // ignore if existing condition same new condition
        val existingCondition = _imageList.value.orEmpty().firstOrNull { it.position == side }

        if (existingCondition?.condition == condition) return

        val images = _imageList.value.orEmpty().filter { it.position != side }

        if (condition.isGood()) {
//            _imageList.value = images.toMutableList()
        } else {
            val mutableImages = images.toMutableList()
            mutableImages.add(ContainerImageUiModel(position = side, condition = condition))
            _imageList.value = mutableImages
        }
    }

    fun onSalesOrderChanged(so: String) {
        _salesOrderNumberData.value = so
    }

    /**
     * handling item on click at recycler view item list
     */
    fun onItemImageListClick(item: ContainerImageUiModel) {
        if (item.imageFilePath.isNullOrBlank()) {
            _openMediaState.value = item
        } else {
            _openImagePreviewState.value = item
        }
    }

    /**
     * handling image selected from view to update to image list
     */
    fun onImageSelected(position: ContainerSidesEnum, file: File) {
        val images = _imageList.value.orEmpty().toMutableList()

        images.forEachIndexed { index, uiModel ->
            if (uiModel.position == position) {
                images[index] = uiModel.copy(imageFilePath = file.absolutePath)
                return@forEachIndexed
            }
        }

        _imageList.value = images
    }

    /**
     * handling result from image preview
     */
    fun onImagePreviewResult(resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            // get image from image preview result
            val filePath = intent.getStringExtra(ImagePreviewActivity.ARG_FILE_PATH)
            // get position from event previous
            val position = _openImagePreviewState.value?.position
            // prepare assign to imageList
            val images = _imageList.value.orEmpty().toMutableList()

            // update to list with new image state
            images.forEachIndexed { index, uiModel ->
                if (uiModel.position == position) {
                    images[index] = uiModel.copy(imageFilePath = filePath)
                    return@forEachIndexed
                }
            }

            _imageList.value = images
        }
    }

    /**
     * prepare container data for result
     */
    fun onSubmit(isFromLocalSales: Boolean = false, isContainerLaden: Boolean) = viewModelScope.launch {
        showLoadingWidget()

        val requestParam = createSaveModel(isContainerLaden)
        val requestLocalSalesParam = createSaveLocalSalesModel()

        if (isFromLocalSales) submitLocalSalesContainer(requestLocalSalesParam)
        else submitContainer(requestParam, isContainerLaden)

        hideLoadingWidget()
    }

    private suspend fun submitContainer(
        requestParam: SaveContainerHistoryRequest,
        isContainerLaden: Boolean
    ) {
        when (val response = saveHistoryUseCase(requestParam, isContainerLaden)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    _submitState.value = requestParam
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
    }

    private suspend fun submitLocalSalesContainer(
        requestParam: SaveLocalSalesRequest
    ) {
        when (val response = saveLocalSalesContainerUseCase(requestParam)) {
            is NetworkResponse.Success -> {
                if (response.body.status == StatusResponseEnum.SUCCESS.status) {
                    _submitLocalSalesState.value = requestParam
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
    }

    private fun createSaveModel(isContainerLaden: Boolean): SaveContainerHistoryRequest {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
        val formatManufactureDate = manufactureDate.value?.let { DateUtil.formatLocalDateToString(it, "yyyy-MM-dd") }
        val typeScan = if (isContainerLaden) containerScanType.value?.type.orEmpty() else null
        return SaveContainerHistoryRequest(
            qrCode = _containerData.value?.uniqueId,
            locationId = _locationData.value?.id,
            userId = user?.id,
            voyageIdOut = _voyageOut.value,
            voyageIdIn = _voyageIn.value,
            sealId = sealNumberData.value,
            soNumber = _salesOrderNumberData.value,
            tarraWeight = tarraWeight.value,
            rightSideCondition = _rightConditionData.value?.type,
            leftSideCondition = _leftConditionData.value?.type,
            roofSideCondition = _upperConditionData.value?.type,
            floorSideCondition = _floorConditionData.value?.type,
            frontDoorCondition = _doorConditionData.value?.type,
            backDoorCondition = _backConditionData.value?.type,
            rightSideConditionImage = getBase64(position = ContainerSidesEnum.RIGHT),
            leftSideConditionImage = getBase64(position = ContainerSidesEnum.LEFT),
            roofSideConditionImage = getBase64(position = ContainerSidesEnum.ROOF),
            floorSideConditionImage = getBase64(position = ContainerSidesEnum.FLOOR),
            frontDoorConditionImage = getBase64(position = ContainerSidesEnum.DOOR),
            backDoorConditionImage = getBase64(position = ContainerSidesEnum.BACK),
            rejectionStatus = rejectionStatus.value?.status,
            remark = containerDefectParam?.containerRemark.orEmpty(),
            description = containerDescription.value.orEmpty(),
            manufactureDate = formatManufactureDate,
            defectPhoto1 = containerDefectParam?.getBase64(1),
            defectPhoto2 = containerDefectParam?.getBase64(2),
            defectPhoto3 = containerDefectParam?.getBase64(3),
            defectPhoto4 = containerDefectParam?.getBase64(4),
            typeScan = typeScan
        )
    }

    private fun createSaveLocalSalesModel(): SaveLocalSalesRequest {
        val user = PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)

        return SaveLocalSalesRequest(
            qrCode = _containerData.value?.uniqueId,
            locationId = _locationData.value?.id,
            userId = user?.id,
            voyageIdOut = _voyageOut.value,
            voyageIdIn = _voyageIn.value,
            sealId = sealNumberData.value,
            soNumber = _salesOrderNumberData.value,
            rightSideCondition = _rightConditionData.value?.type,
            leftSideCondition = _leftConditionData.value?.type,
            roofSideCondition = _upperConditionData.value?.type,
            floorSideCondition = _floorConditionData.value?.type,
            frontDoorCondition = _doorConditionData.value?.type,
            backDoorCondition = _backConditionData.value?.type,
            rightSideConditionImage = getBase64(position = ContainerSidesEnum.RIGHT),
            leftSideConditionImage = getBase64(position = ContainerSidesEnum.LEFT),
            roofSideConditionImage = getBase64(position = ContainerSidesEnum.ROOF),
            floorSideConditionImage = getBase64(position = ContainerSidesEnum.FLOOR),
            frontDoorConditionImage = getBase64(position = ContainerSidesEnum.DOOR),
            backDoorConditionImage = getBase64(position = ContainerSidesEnum.BACK),
        )
    }

    private fun getBase64(position: ContainerSidesEnum): String? {
        val filePath = _imageList.value?.firstOrNull { it.position == position }?.imageFilePath

        if (filePath.isNullOrBlank()) return null

        val file = File(filePath)

        return Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    }
}