package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.Cleanable

class FilesListViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<BoxesContainer.PathEntries>()
    val liveData: LiveData<BoxesContainer.PathEntries> = data

    fun setFilesList(files: BoxesContainer.PathEntries?) {
        data.value = files
    }

    override fun clear() {
        if (liveData.value != null) setFilesList(null)
    }
}
