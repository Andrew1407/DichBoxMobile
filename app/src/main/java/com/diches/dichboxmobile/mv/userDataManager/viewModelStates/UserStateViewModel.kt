package com.diches.dichboxmobile.mv.userDataManager.viewModelStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.mv.Cleanable

class UserStateViewModel: ViewModel(), Cleanable {
    private val data = MutableLiveData<Pair<String?, String?>>()
    val namesState: LiveData<Pair<String?, String?>> = data

    fun setState(names: Pair<String?, String?>) {
        data.value = names
    }

    override fun clear() {
        data.value = Pair(null, null)
    }
}