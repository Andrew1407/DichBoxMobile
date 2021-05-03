package com.diches.dichboxmobile.mv.usersSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable

class UsersSearchViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<List<UserContainer.FoundUser>>()
    val liveData: LiveData<List<UserContainer.FoundUser>> = data

    fun setUsers(dataVal: List<UserContainer.FoundUser>) {
        data.value = dataVal
    }

    override fun clear() {
        if (data.value != null) data.value = emptyList()
    }
}