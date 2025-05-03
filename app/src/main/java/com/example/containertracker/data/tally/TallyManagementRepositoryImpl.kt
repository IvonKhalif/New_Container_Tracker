package com.example.containertracker.data.tally

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.data.tally.request.SavePalletRequest
import com.example.containertracker.data.tally.request.SavePhotoTallyRequest
import com.example.containertracker.data.tally.request.SaveTallySheetRequest
import com.example.containertracker.data.tally.request.SubmitStatusTallyRequest
import com.example.containertracker.data.tally.request.UpdatePalletRequest
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.domain.tally.TallyManagementRepository
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitListResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

class TallyManagementRepositoryImpl(private val service: TallyManagementService): TallyManagementRepository {
    override suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String,
        accountId: String?
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse> {
        return service.getContainer(qrCode, containerCode, flag, accountId)
    }

    override suspend fun saveTallySheet(request: SaveTallySheetRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.saveTallySheet(request)
    }

    override suspend fun savePhoto(
        tallySheetId: String,
        itemId: String?,
        photo: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.savePhoto(SavePhotoTallyRequest( tallySheetId, itemId, photo))
    }

    override suspend fun scanSPM(
        barcode: String,
        accountId: String?,
        tallySheetId: String,
        flag: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.scanSPM(barcode, accountId, "1", flag)
    }

    override suspend fun scanPallet(
        barcode: String,
        accountId: String?,
        tallySheetId: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.scanPallet(barcode, accountId, tallySheetId)
    }

    override suspend fun getPallets(
        tallySheetId: String
    ): NetworkResponse<RetrofitListResponse<PalletResponse>, GenericErrorResponse> {
        return service.listPallet(tallySheetId)
    }

    override suspend fun getPalletDetail(idTally: String): NetworkResponse<RetrofitResponse<PalletResponse>, GenericErrorResponse> {
        return service.detailPallet(idTally)
    }

    override suspend fun updatePallet(request: UpdatePalletRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.updatePallet(request)
    }

    override suspend fun savePallet(request: SavePalletRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.savePallet(request)
    }

    override suspend fun deletePallet(idTallySheetPallet: String): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.deletePallet(idTallySheetPallet)
    }

    override suspend fun getDraftTallyList(accountId: String): NetworkResponse<RetrofitResponse<List<TallySheetDrafts>>, GenericErrorResponse> {
        return service.draftTallyList(accountId)
    }

    override suspend fun getTallySheetDetail(idTally: String): NetworkResponse<RetrofitResponse<TallySheetDetailResponse>, GenericErrorResponse> {
        return service.getTallySheetDetail(idTally)
    }

    override suspend fun deleteTallySheet(accountId: String,idTally: String): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.deleteTallySheet(accountId,idTally)
    }

    override suspend fun submitStatusTally(request: SubmitStatusTallyRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse> {
        return service.submitStatus(request)
    }
}