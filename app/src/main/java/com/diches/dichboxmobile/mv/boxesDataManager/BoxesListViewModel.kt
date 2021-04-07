package com.diches.dichboxmobile.mv.boxesDataManager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.BoxesContainer

class BoxesListViewModel : ViewModel() {
    private val data = MutableLiveData<Pair<List<BoxesContainer.BoxDataListItem>, List<BoxesContainer.BoxDataListItem>>>()
    val liveData: LiveData<Pair<List<BoxesContainer.BoxDataListItem>, List<BoxesContainer.BoxDataListItem>>> = data

    fun setBoxesList(dataVal: Pair<List<BoxesContainer.BoxDataListItem>, List<BoxesContainer.BoxDataListItem>>) {
        data.value = dataVal
    }
}