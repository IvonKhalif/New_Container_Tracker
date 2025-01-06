package com.example.containertracker.ui.media.image_preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Created by yovi.putra on 23/07/22"
 * Project name: Container Tracker
 **/


class ImagePreviewViewModel : ViewModel() {

    private val _imagePath = MutableLiveData<String?>()
    val imagePath: LiveData<String?> get() = _imagePath

    private val _resultState = MutableLiveData<String?>()
    val resultState: LiveData<String?> get() = _resultState

    private var isImageLoadError = false

    fun onImagePathChanged(path: String?) = viewModelScope.launch {
        _imagePath.value = path
    }

    fun onRemove() = viewModelScope.launch {
        _resultState.value = null
    }

    fun onDone() = viewModelScope.launch {
        _resultState.value = _imagePath.value
    }

    fun onLoadImageResult(isError: Boolean) {
        isImageLoadError = isError
    }

    fun onContainerImageClick() = viewModelScope.launch {
        if (isImageLoadError) {
            _imagePath.value = _imagePath.value.orEmpty()
        }
    }
}