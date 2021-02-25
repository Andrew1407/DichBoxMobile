package com.diches.dichboxmobile.mv

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignViewModel : ViewModel() {
    val isSignUp = MutableLiveData<Boolean>()
}