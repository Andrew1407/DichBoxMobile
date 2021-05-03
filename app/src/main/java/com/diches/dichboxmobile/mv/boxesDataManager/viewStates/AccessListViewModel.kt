package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable

abstract class AccessListViewModel : ViewModel(), Cleanable {
    private val dataAdded = MutableLiveData<List<UserContainer.FoundUser>>()
    private val dataFound = MutableLiveData<List<UserContainer.FoundUser>>()
    val added: LiveData<List<UserContainer.FoundUser>> = dataAdded
    val found: LiveData<List<UserContainer.FoundUser>> = dataFound

    fun setFoundUsers(data: List<UserContainer.FoundUser>) {
        dataFound.value = data
    }

    fun setAddedUsers(data: List<UserContainer.FoundUser>) {
        dataAdded.value = data
    }

    override fun clear() {
        if (dataAdded.value != null) dataAdded.value = emptyList()
        if (dataFound.value != null) dataFound.value = emptyList()
    }
}

class EditorsViewModel : AccessListViewModel()
class ViewersViewModel : AccessListViewModel()
