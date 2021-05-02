package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FileRedirectorViewModel : ViewModel() {
    private val data = MutableLiveData<Boolean>()
    val isRedirected: LiveData<Boolean> = data

    fun setRedirected(redirected: Boolean) {
        data.value = redirected
    }
}