package com.diches.dichboxmobile.mv.userDataManager.viewModelStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserStateViewModel: ViewModel() {
    private val data = MutableLiveData<Pair<String?, String?>>()
    val namesState: LiveData<Pair<String?, String?>> = data

    fun setState(names: Pair<String?, String?>) {
        data.value = names
    }
}