package com.example.containertracker.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

open class PostLiveData<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (!hasObservers()) {
            super.observe(owner, Observer { data ->
                if (data == null) return@Observer
                observer.onChanged(data)
                value = null
            })
        }
    }

    @MainThread
    open fun post(data: T) {
        value = data
        value = null
    }
}