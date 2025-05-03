package com.example.containertracker.domain.tally

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.data.tally.request.SavePalletRequest
import com.example.containertracker.data.tally.request.SaveTallySheetRequest
import com.example.containertracker.data.tally.request.SubmitStatusTallyRequest
import com.example.containertracker.data.tally.request.UpdatePalletRequest
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitListResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse

interface TallyManagementRepository {
    suspend fun getContainer(
        qrCode: String,
        containerCode: String?,
        flag: String,
        accountId: String?
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    suspend fun saveTallySheet(request: SaveTallySheetRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun savePhoto(
        tallySheetId: String,
        itemId: String?,
        photo: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun scanSPM(
        barcode: String,
        accountId: String?,
        tallySheetId: String,
        flag: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun scanPallet(
        barcode: String,
        accountId: String?,
        tallySheetId: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun getPallets(
        tallySheetId: String
    ): NetworkResponse<RetrofitListResponse<PalletResponse>, GenericErrorResponse>

    suspend fun getPalletDetail(
        idTally: String,
    ): NetworkResponse<RetrofitResponse<PalletResponse>, GenericErrorResponse>

    suspend fun updatePallet(request: UpdatePalletRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun savePallet(request: SavePalletRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun deletePallet(
        idTallySheetPallet: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun getDraftTallyList(
        accountId: String,
    ): NetworkResponse<RetrofitResponse<List<TallySheetDrafts>>, GenericErrorResponse>

    suspend fun getTallySheetDetail(
        idTally: String,
    ): NetworkResponse<RetrofitResponse<TallySheetDetailResponse>, GenericErrorResponse>

    suspend fun deleteTallySheet(
        accountId: String,
        idTally: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    suspend fun submitStatusTally(request: SubmitStatusTallyRequest): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}
