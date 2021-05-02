package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer

abstract class TempAccessListsViewModel : ViewModel() {
    private val editedAdded = MutableLiveData<List<UserContainer.FoundUser>>()
    val added: LiveData<List<UserContainer.FoundUser>> = editedAdded

    fun setEditedAddedUsers(data: List<UserContainer.FoundUser>) {
        editedAdded.value = data
    }
}

class EditorsCopyViewModel : TempAccessListsViewModel()
class ViewersCopyViewModel : TempAccessListsViewModel()
