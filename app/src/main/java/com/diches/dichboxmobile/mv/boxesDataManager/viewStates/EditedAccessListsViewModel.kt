package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable

abstract class TempAccessListsViewModel : ViewModel(), Cleanable {
    private val editedAdded = MutableLiveData<List<UserContainer.FoundUser>>()
    val added: LiveData<List<UserContainer.FoundUser>> = editedAdded

    fun setEditedAddedUsers(data: List<UserContainer.FoundUser>) {
        editedAdded.value = data
    }

    override fun clear() {
        if (editedAdded.value != null) editedAdded.value = emptyList()
    }
}

class EditorsCopyViewModel : TempAccessListsViewModel()
class ViewersCopyViewModel : TempAccessListsViewModel()
