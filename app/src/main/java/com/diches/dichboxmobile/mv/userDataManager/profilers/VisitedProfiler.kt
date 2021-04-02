package com.diches.dichboxmobile.mv.userDataManager.profilers

import android.widget.TextView
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VisitedProfiler(
        private val visitorState: UserStateViewModel,
        private val subscribeButton: TextView
) : UserProfiler() {
    private val api = UserAPI()

    fun handleSubscriptionView(followersView: TextView, userDataState: UserDataViewModel): VisitedProfiler {
        val follower = userDataState.liveData.value!!.follower
        decorateSubscriptionView(follower)

        subscribeButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val action = subscribeButton.text.toString()
                val (st, res) = subscriptionsRequest(action)
                if (st != 200) return@launch
                val data = res as UserContainer.SubsActionRes
                val followersParams = Pair(data.followers.toString(), AppColors.GRAY.raw)
                decorateView(followersView, "followers: ", followersParams)
                decorateSubscriptionView(data.follower)
                val newUserState = userDataState.liveData.value!!.copy(
                        followers = data.followers,
                        follower = data.follower
                )
                refreshData(newUserState)
                userDataState.setUserData(newUserState)
            }
        }

        return this
    }

    private fun decorateSubscriptionView(follower: Boolean) {
        subscribeButton.text = if (follower) "unsubscribe" else "subscribe"
        val btnTextColor = if (follower) AppColors.PURPLE else AppColors.GREEN
        subscribeButton.setTextColor(btnTextColor.raw)
    }

    private suspend fun subscriptionsRequest(action: String): Pair<Int, UserContainer> {
        val (personName, subscriptionName) = visitorState.namesState.value!!
        val responseExtended = true
        val subBody = UserContainer.SubsAction(
                action, personName!!, subscriptionName!!, responseExtended
        )
        return api.subscribeAction(subBody)
    }
}