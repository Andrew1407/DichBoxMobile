package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.mv.Cleanable

class CurrentBoxViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<String?>()
    val boxName: LiveData<String?> = data

    fun setCurrentBox(name: String?) {
        data.value = name
    }

    override fun clear() {
        if (boxName.value != null) setCurrentBox(null)
    }
}