package com.diches.dichboxmobile.mv.settings

import android.content.Context
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable
import kotlinx.coroutines.*

class RemoveAccountOption(
        private val context: Context,
        viewStates: List<Cleanable>
) : SignOutOption(context, viewStates) {
    private val api = UserAPI()

    override fun onOkClick() {
        context.openFileInput("signed.txt").use { stream ->
            val name = stream?.bufferedReader().use { it?.readText() }
            val confirmation = "permitted"
            val rmBody = UserContainer.RemovedUser(name!!, confirmation)
            CoroutineScope(Dispatchers.Main).launch {
                val (st, res) = withContext(Dispatchers.IO) { api.removeUser(rmBody) }
                val (removed) = res as UserContainer.RemovedRes
                if (st == 200 && removed) super.onOkClick()
            }
        }
    }
}