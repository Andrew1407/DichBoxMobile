package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.Cleanable

class BoxesListViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<Pair<List<BoxesContainer.BoxDataListItem>, List<BoxesContainer.BoxDataListItem>>>()
    val liveData: LiveData<Pair<List<BoxesContainer.BoxDataListItem>, List<BoxesContainer.BoxDataListItem>>> = data

    fun setBoxesList(dataVal: Pair<List<BoxesContainer.BoxDataListItem>, List<BoxesContainer.BoxDataListItem>>) {
        data.value = dataVal
    }

    override fun clear() {
        if (data.value != null) data.value = null
    }
}