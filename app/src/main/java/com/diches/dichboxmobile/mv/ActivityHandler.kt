package com.diches.dichboxmobile.mv

import android.app.Activity
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.UserDataFetcher
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.runBlocking

class ActivityHandler(
    private val activity: Activity,
    private val usernamesViewModel: UserStateViewModel,
    private val userDataViewModel: UserDataViewModel
) {
    private val userApi = UserAPI()

    fun fetchUsername(userDataFetcher: UserDataFetcher) {
        val isSigned = activity.getFileStreamPath("signed.txt")!!.exists()
        if (!isSigned) return usernamesViewModel.setState(Pair(null, null))
        readSignedFile { uuid ->
            val name = requestUsername(uuid)
            if (name.isEmpty()) return@readSignedFile
            usernamesViewModel.setState(Pair(name, name))
            userDataFetcher.fillUserViewModel(userDataViewModel, usernamesViewModel)
        }
    }

    fun checkModifiedUsername() {
        val username = usernamesViewModel.namesState.value!!.first ?: return
        readSignedFile { uuid ->
            val name = requestUsername(uuid)
            if (name.isEmpty()) return@readSignedFile
            usernamesViewModel.setState(Pair(name, name))
        }
    }

    private fun readSignedFile(handler: (String) -> Unit) {
        val exists = activity.getFileStreamPath("signed.txt")!!.exists()
        if (!exists) return
        activity.openFileInput("signed.txt").use { stream ->
            val uuid = stream?.bufferedReader().use { it?.readText() }!!.reversed()
            handler(uuid)
        }
    }

    private fun requestUsername(uuid: String): String {
        val container = UserContainer.SignedContainer(uuid = uuid)
        val (st, res) = runBlocking { userApi.getUsernameByUuid(container) }
        if (Statuses.OK.eqNot(st)) return ""
        val (name, _) = (res as UserContainer.SignedContainer)
        return name!!
    }
}