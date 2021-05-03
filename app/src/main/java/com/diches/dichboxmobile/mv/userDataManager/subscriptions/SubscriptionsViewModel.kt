package com.diches.dichboxmobile.mv.userDataManager.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable

class SubscriptionsViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>>>()
    val liveData: LiveData<Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>>> = data

    fun setSubs(dataVal: Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>>) {
        data.value = dataVal
    }

    override fun clear() {
        if (data.value != null) data.value = null
    }
}