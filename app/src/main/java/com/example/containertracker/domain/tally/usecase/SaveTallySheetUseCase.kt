package com.example.containertracker.domain.tally.usecase

import com.example.containertracker.data.tally.request.SaveTallySheetRequest
import com.example.containertracker.domain.tally.TallyManagementRepository
import com.example.containertracker.utils.enums.TallyConditionCheckedEnum
import com.example.containertracker.utils.extension.orFalse
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class SaveTallySheetUseCase(private val repository: TallyManagementRepository) {
    suspend operator fun invoke(
        idTrackingContainer: String?,
        idSalesOrderDetail: String?,
        tallyName: String?,
        buyerName: String?,
        forkliftDriver: String?,
        stuffer: String?,
        loadingDate: String?,
        startTime: String?,
        finishTime: String?,
        qtyPallet: String?,
        qtyBags: String?,
        palletId: String?,
        roofCondition: String?,
        floorCondition: String?,
        wallCondition: String?,
        doorCondition: String?,
        isLeftLockBarChecked: Boolean?,
        isRightLockBarChecked: Boolean?,
        isPlasticWrappedYesChecked: Boolean?,
        isPlasticWrappedNoChecked: Boolean?,
        isTaliChecked: Boolean?,
        polyfoam: String?,
        cartoonSheet1x1: Int?,
        cartoonSheet2x1: Int?,
        stripingBelt: Int?,
        isStripingBentChecked: Boolean?,
        polywood: String?,
        paperBag: String?,
        isNetralPaperBag: Boolean?,
        isSociPaperBag: Boolean?,
        isScplPaperBag: Boolean?,
        jumboBag: String?,
        stellDrum: String?,
        isBlueStellDrum: Boolean?,
        isGreyStellDrum: Boolean?,
        isGreenStellDrum: Boolean?,
        hdpeDrum: String?,
        ibcTank: String?,
        isPalletizedChecked: Boolean?,
        isUnPalletizedChecked: Boolean?,
        isShipMarkChecked: Boolean?,
        isGHSChecked: Boolean?,
        isDGChecked: Boolean?,
        isPSNChecked: Boolean?,
        totalLabel: String?,
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return repository.saveTallySheet(
            SaveTallySheetRequest(
                salesOrderDetailId = idSalesOrderDetail,
                idTracking = idTrackingContainer,
                buyer = buyerName,
                tallyName = tallyName,
                driverForklift = forkliftDriver,
                stuffer = stuffer,
                loadingDate = loadingDate,
                startTime = startTime,
                finishTime = finishTime,
                quantityPallet = qtyPallet?.toIntOrNull(),
                quantityBags = qtyBags,
                idPallet = palletId,
                roofCondition = roofCondition,
                floorCondition = floorCondition,
                wallCondition = wallCondition,
                doorCondition = doorCondition,
                leftLockBar = conditionChecked(isLeftLockBarChecked),
                rightLockBar = conditionChecked(isRightLockBarChecked),
                plasticWrapped = isPlasticWrapped(isPlasticWrappedYesChecked, isPlasticWrappedNoChecked),
                stripingBelt = stripingBelt,
                stripingBent = conditionChecked(isStripingBentChecked),
                tali = conditionChecked(isTaliChecked),
                cartoonSheet2x1 = cartoonSheet2x1,
                cartoonSheet1x1 = cartoonSheet1x1,
                polyfoam = polyfoam?.toIntOrNull(),
                polywood = polywood?.toIntOrNull(),
                paperBag = paperBag?.toIntOrNull(),
                jumboBag = jumboBag?.toIntOrNull(),
                stellDrum = stellDrum?.toIntOrNull(),
                hdpeDrum = hdpeDrum?.toIntOrNull(),
                ibcTank = ibcTank?.toIntOrNull(),
                palletized = conditionChecked(isPalletizedChecked),
                unpalletized = conditionChecked(isUnPalletizedChecked),
                shipMark = conditionChecked(isShipMarkChecked),
                ghs = conditionChecked(isGHSChecked),
                dg = conditionChecked(isDGChecked),
                psn = conditionChecked(isPSNChecked),
                totalLabel = totalLabel?.toIntOrNull(),
                netralPaperBag = conditionChecked(isNetralPaperBag),
                sociPaperBag = conditionChecked(isSociPaperBag),
                scplPaperBag = conditionChecked(isScplPaperBag),
                blueStellDrum = conditionChecked(isBlueStellDrum),
                greyStellDrum = conditionChecked(isGreyStellDrum),
                greenStellDrum = conditionChecked(isGreenStellDrum)
            )
        )
    }

    private fun isPlasticWrapped(isYesChecked: Boolean?, isNoChecked: Boolean?) =
        when {
            isYesChecked.orFalse() -> TallyConditionCheckedEnum.YES.desc
            isNoChecked.orFalse() -> TallyConditionCheckedEnum.NO.desc
            else -> TallyConditionCheckedEnum.NO.desc
        }

    private fun conditionChecked(isChecked: Boolean?) =
        if (isChecked.orFalse()) TallyConditionCheckedEnum.Y.desc else TallyConditionCheckedEnum.N.desc
}