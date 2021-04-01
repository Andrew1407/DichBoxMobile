package com.diches.dichboxmobile.mv.userDataManager.viewModelStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditedViewModel: ViewModel() {
    private val data = MutableLiveData<Boolean>()
    val isEdited: LiveData<Boolean> = data

    fun setEdited(edited: Boolean) {
        data.value = edited
    }
}