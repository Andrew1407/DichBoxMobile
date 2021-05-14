package com.diches.dichboxmobile.mv.userDataManager

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.*

class UserDataFetcher {
    private lateinit var userStateVM: UserStateViewModel
    private lateinit var userDataVM: UserDataViewModel

    private val api = UserAPI()

    fun handleRefresh(refreshLayout: SwipeRefreshLayout, clb: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val namesBody = userStateVM.namesState.value!!
            val userData = withContext(Dispatchers.IO) { fetchUserData(namesBody) }
            if (userData.name != namesBody.second) return@launch
            userDataVM.setUserData(userData)
            clb()
            refreshLayout.isRefreshing = false
        }
    }
    fun fillUserViewModel(
        userDataViewModel: UserDataViewModel,
        userStateViewModel: UserStateViewModel
    ) {
        userDataVM = userDataViewModel
        userStateVM = userStateViewModel
        val namesBody = userStateVM.namesState.value!!
        val userData = runBlocking {
            withContext(Dispatchers.IO) { fetchUserData(namesBody) }
        }
        userDataVM.setUserData(userData)
    }

    private suspend fun fetchUserData(containerNames: Pair<String?, String?>): UserContainer.UserData {
        val resData = UserContainer.FindContainer(
                username = containerNames.first,
                pathName = containerNames.second!!
        )
        val (_, data) = api.findUser(resData)
        return data as UserContainer.UserData
    }
}