package com.example.containertracker.ui.home.containercondition.models

import android.os.Parcelable
import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.location.models.Location
import com.example.containertracker.ui.home.containerdefect.ContainerDefectParam
import com.example.containertracker.utils.enums.ContainerRejectionStatusEnum
import com.example.containertracker.utils.enums.TypeScanEnum
import com.example.containertracker.widget.select_image.adapter.GenericSelectImageUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContainerConditionParam(
    val container: Container,
    val location: Location?,
    var voyageIdIn: String? = null,
    var voyageIdOut: String? = null,
    var rejectionStatusEnum: ContainerRejectionStatusEnum = ContainerRejectionStatusEnum.RELEASE,
    var isLocalSales: Boolean = false,
    var containerDefectParam: ContainerDefectParam? = null,
    var containerLadenScanType: TypeScanEnum? = null,
    val isContainerLaden: Boolean = false
): Parcelable