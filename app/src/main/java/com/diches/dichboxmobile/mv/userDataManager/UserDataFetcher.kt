package com.diches.dichboxmobile.mv.userDataManager

import android.content.Context
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.runBlocking

class UserDataFetcher {
    private val api = UserAPI()

    fun fillUserViewModel(viewModel: UserDataViewModel, context: Context) {
        context.openFileInput("signed.txt").use { stream ->
            val name = stream?.bufferedReader().use { it?.readText() }
            runBlocking {
                val userData = fetchUserData(name!!)
                viewModel.setUserData(userData)
            }
        }
    }

    private suspend fun fetchUserData(name: String): UserContainer.UserData {
        val resData = UserContainer.FindContainer(name, name)
        val (st, data) = api.findUser(resData)
        return data as UserContainer.UserData
    }
}