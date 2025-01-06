package com.example.containertracker.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class ActionLiveData : MutableLiveData<Boolean>() {
    fun post() {
        value = true
        value = false
    }

    fun postAction() {
        postValue(true)
    }

    fun observeAction(owner: LifecycleOwner, action: () -> Unit) {
        super.observe(owner) {
            if (value == true) {
                action()
                postValue(false)
            }
        }
    }
}