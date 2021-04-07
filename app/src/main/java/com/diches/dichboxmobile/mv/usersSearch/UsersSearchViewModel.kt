package com.diches.dichboxmobile.mv.usersSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer

class UsersSearchViewModel : ViewModel() {
    private val data = MutableLiveData<List<UserContainer.FoundUser>>()
    val liveData: LiveData<List<UserContainer.FoundUser>> = data

    fun setUsers(dataVal: List<UserContainer.FoundUser>) {
        data.value = dataVal
    }
}