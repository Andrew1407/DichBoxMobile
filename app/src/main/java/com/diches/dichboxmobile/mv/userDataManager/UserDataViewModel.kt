package com.diches.dichboxmobile.mv.userDataManager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer

class UserDataViewModel : ViewModel() {
    private val data = MutableLiveData<UserContainer.UserData>()
    val liveData: LiveData<UserContainer.UserData> = data

    fun setUserData(dataVal: UserContainer.UserData?) {
        data.value = dataVal
    }
}