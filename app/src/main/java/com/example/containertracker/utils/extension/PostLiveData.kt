package com.example.containertracker.utils.extension

import androidx.lifecycle.LifecycleOwner
import com.example.containertracker.utils.PostLiveData

fun <T> PostLiveData<T?>.observeNonNull(owner: LifecycleOwner, observer: (T) -> Unit) {
    this.observe(owner) {
        it?.let {
            observer(it)
        }
    }
}