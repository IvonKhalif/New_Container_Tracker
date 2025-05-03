package com.example.containertracker.data.tally

import com.example.containertracker.data.container.models.Container
import com.example.containertracker.data.tally.request.SaveTallySheetRequest
import com.example.containertracker.data.tally.response.TallySheetDetailResponse
import com.example.containertracker.data.tally.response.TallySheetDrafts
import com.example.containertracker.data.tally.request.SavePalletRequest
import com.example.containertracker.data.tally.request.SavePhotoTallyRequest
import com.example.containertracker.data.tally.request.SubmitStatusTallyRequest
import com.example.containertracker.data.tally.request.UpdatePalletRequest
import com.example.containertracker.data.tally.response.PalletResponse
import com.example.containertracker.utils.constants.ContentTypeConstant
import com.example.containertracker.utils.response.GenericErrorResponse
import com.example.containertracker.utils.response.RetrofitListResponse
import com.example.containertracker.utils.response.RetrofitResponse
import com.example.containertracker.utils.response.RetrofitStatusResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface TallyManagementService {
    @GET("v2/tlms/scan-container")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getContainer(
        @Query("qr_code") qrCode: String,
        @Query("code_container") containerCode: String?,
        @Query("flag") flagScan: String,
        @Query("id_account") accountId: String?
    ): NetworkResponse<RetrofitResponse<Container>, GenericErrorResponse>

    @GET("v2/tlms/tally-sheet")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun draftTallyList(
        @Query("id_account") accountId: String?
    ): NetworkResponse<RetrofitResponse<List<TallySheetDrafts>>, GenericErrorResponse>

    @GET("v2/tlms/tally-sheet/detail")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun getTallySheetDetail(
        @Query("id_tally_sheet") idTallySheet: String
    ): NetworkResponse<RetrofitResponse<TallySheetDetailResponse>, GenericErrorResponse>

    @POST("v2/tlms/tally-sheet/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun saveTallySheet(
        @Body request: SaveTallySheetRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/tlms/tally-sheet/delete")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun deleteTallySheet(
        @Query("id_account") accountId: String?,
        @Query("id_tally_sheet") idTallySheet: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @POST("v2/tlms/tally-sheet/save-photo")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun savePhoto(
        @Body request: SavePhotoTallyRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/tlms/scan-spm")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun scanSPM(
        @Query("barcode") barcode: String,
        @Query("id_account") accountId: String?,
        @Query("id_tally_sheet") tallySheetId: String,
        @Query("flag") flagScan: String,
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/tlms/pallet/scan")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun scanPallet(
        @Query("barcode") barcode: String,
        @Query("id_account") accountId: String?,
        @Query("id_tally_sheet") tallySheetId: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/tlms/pallet/")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun listPallet(
        @Query("id_tally_sheet") tallySheetId: String
    ): NetworkResponse<RetrofitListResponse<PalletResponse>, GenericErrorResponse>

    @GET("v2/tlms/pallet/detail")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun detailPallet(
        @Query("id_tally_sheet_pallet") tallySheetPalletId: String
    ): NetworkResponse<RetrofitResponse<PalletResponse>, GenericErrorResponse>

    @POST("v2/tlms/pallet/update")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun updatePallet(
        @Body request: UpdatePalletRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @POST("v2/tlms/pallet/save")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun savePallet(
        @Body request: SavePalletRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @GET("v2/tlms/pallet/delete")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun deletePallet(
        @Query("id_tally_sheet_pallet") idTallySheetPallet: String
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>

    @POST("v2/tlms/tally-sheet/submit")
    @Headers(ContentTypeConstant.CONTENT_TYPE_JSON)
    suspend fun submitStatus(
        @Body request: SubmitStatusTallyRequest
    ): NetworkResponse<RetrofitStatusResponse, GenericErrorResponse>
}