package com.example.containertracker.ui.container.detail

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.containertracker.base.BaseViewModel
import com.example.containertracker.data.container.models.ContainerDetail
import com.example.containertracker.domain.container.ContainerQRUseCase
import com.haroldadmin.cnradapter.NetworkResponse
import com.printer.command.EscCommand
import com.printer.command.LabelCommand
import kotlinx.coroutines.launch
import kotlin.math.ceil

class ContainerDetailViewModel(
    private val getContainerQRUseCase: ContainerQRUseCase
): BaseViewModel() {

    private val _qrContainerLiveData = MutableLiveData<ContainerDetail>()
    val qrContainerLiveData: LiveData<ContainerDetail> get() = _qrContainerLiveData

    private val _printData = MutableLiveData<LabelCommand>()
    val printData: LiveData<LabelCommand> get() = _printData

    private val _containerImageBitmap = MutableLiveData<Bitmap?>()
    val containerImageBitmap: LiveData<Bitmap?> = _containerImageBitmap

    fun getContainerQR(id: String){
        showLoadingWidget()
        viewModelScope.launch {
            when (val response = getContainerQRUseCase(id)) {
                is NetworkResponse.Success -> {
                    response.body.data.let {
                        hideLoadingWidget()
                        _qrContainerLiveData.value = it
                    }
                }
                is NetworkResponse.ServerError -> {
                    genericErrorLiveData.value = response.body
                }
                is NetworkResponse.NetworkError -> {
                    networkErrorLiveData.value = response.error
                }
                is NetworkResponse.UnknownError -> _serverErrorState.value = response.body
            }
            hideLoadingWidget()
        }
    }

    fun setBitmap(bitmap: Bitmap?) {
        _containerImageBitmap.value = bitmap
    }

    fun print() {
        viewModelScope.launch {
            composePrintData()
        }
    }
    /**
     * compose print data
     */
    private fun composePrintData() {
        val tsc = LabelCommand()
        val qrcode = _containerImageBitmap.value
        val code = _qrContainerLiveData.value?.codeContainer.orEmpty()

        tsc.addReference(0, 0) // atur koordinat asal
        tsc.addTear(EscCommand.ENABLE.ON) // Mode sobek aktif
        tsc.addCls() // hapus buffer cetak

        if (qrcode != null) {
            tsc.addBitmap(
                15,
                70,
                LabelCommand.BITMAP_MODE.OVERWRITE,
                550,
                qrcode
            )
        }

        tsc.addText(
            75,
            675,
            LabelCommand.FONTTYPE.FONT_1,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_4,
            LabelCommand.FONTMUL.MUL_4,
            code
        )

        tsc.addPrint(1, 1) // label cetak
        tsc.addSound(2, 100) // Buzzer berbunyi setelah mencetak label

        _printData.value = tsc
    }
}