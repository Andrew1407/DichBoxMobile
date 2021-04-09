package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BoxEditedViewModel : ViewModel() {
    private val data = MutableLiveData<Boolean>()
    val isEdited: LiveData<Boolean> = data

    fun setEdited(edited: Boolean) {
        data.value = edited
    }
}