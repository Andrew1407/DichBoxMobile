package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.Cleanable

class BoxDataViewModel: ViewModel(), Cleanable {
    private val data = MutableLiveData<BoxesContainer.BoxData>()
    val liveData: LiveData<BoxesContainer.BoxData> = data

    fun setBoxData(dataVal: BoxesContainer.BoxData?) {
        data.value = dataVal
    }

    override fun clear() {
        if (liveData.value != null) setBoxData(null)
    }
}