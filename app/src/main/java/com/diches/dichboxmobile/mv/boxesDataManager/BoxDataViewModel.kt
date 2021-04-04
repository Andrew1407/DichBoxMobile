package com.diches.dichboxmobile.mv.boxesDataManager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.BoxesContainer

class BoxDataViewModel: ViewModel() {
    private val data = MutableLiveData<BoxesContainer.BoxData>()
    val liveData: LiveData<BoxesContainer.BoxData> = data

    fun setBoxData(dataVal: BoxesContainer.BoxData?) {
        data.value = dataVal
    }
}