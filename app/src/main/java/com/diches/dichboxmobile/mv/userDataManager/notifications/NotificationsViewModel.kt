package com.diches.dichboxmobile.mv.userDataManager.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable

class NotificationsViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<MutableList<UserContainer.NotificationData>>()
    val liveData: LiveData<MutableList<UserContainer.NotificationData>> = data

    fun setNotifications(dataVal: MutableList<UserContainer.NotificationData>) {
        data.value = dataVal
    }

    override fun clear() {
        if (data.value != null) data.value = null
    }
}