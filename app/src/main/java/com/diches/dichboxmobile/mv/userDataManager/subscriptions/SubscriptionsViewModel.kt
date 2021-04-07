package com.diches.dichboxmobile.mv.userDataManager.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer

class SubscriptionsViewModel : ViewModel() {
    private val data = MutableLiveData<Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>>>()
    val liveData: LiveData<Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>>> = data

    fun setSubs(dataVal: Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>>) {
        data.value = dataVal
    }
}