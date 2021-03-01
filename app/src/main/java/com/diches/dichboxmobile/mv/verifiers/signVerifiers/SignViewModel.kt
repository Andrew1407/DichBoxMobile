package com.diches.dichboxmobile.mv.verifiers.signVerifiers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignViewModel : ViewModel(){
    private val signed = MutableLiveData<Boolean>()
    val isSigned: LiveData<Boolean> = signed

    fun setIsSigned(isSigned: Boolean) {
        signed.value = isSigned
    }
}