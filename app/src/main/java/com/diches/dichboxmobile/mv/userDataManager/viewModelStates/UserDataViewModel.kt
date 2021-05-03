package com.diches.dichboxmobile.mv.userDataManager.viewModelStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable

class UserDataViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<UserContainer.UserData>()
    val liveData: LiveData<UserContainer.UserData> = data

    fun setUserData(dataVal: UserContainer.UserData?) {
        data.value = dataVal
    }

    override fun clear() {
        if (data.value != null) data.value = null
    }
}