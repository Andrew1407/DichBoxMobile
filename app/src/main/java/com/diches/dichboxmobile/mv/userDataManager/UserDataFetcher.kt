package com.diches.dichboxmobile.mv.userDataManager

import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.*

class UserDataFetcher {
    private val api = UserAPI()

    fun fillUserViewModel(userDataVM: UserDataViewModel, userStateVM: UserStateViewModel) {
        val namesBody = userStateVM.namesState.value!!
        val userData = runBlocking{
            withContext(Dispatchers.IO) { fetchUserData(namesBody) }
        }
        userDataVM.setUserData(userData)
    }

    private suspend fun fetchUserData(containerNames: Pair<String?, String?>): UserContainer.UserData {
        val resData = UserContainer.FindContainer(
                username = containerNames.first,
                pathName = containerNames.second!!
        )
        val (st, data) = api.findUser(resData)
        return data as UserContainer.UserData
    }
}