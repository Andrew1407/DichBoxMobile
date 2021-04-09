package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrentBoxViewModel : ViewModel() {
    private val data = MutableLiveData<String?>()
    val boxName: LiveData<String?> = data

    fun setCurrentBox(name: String?) {
        data.value = name
    }
}