package com.diches.dichboxmobile.mv.userDataManager.profilers

import android.widget.TextView
import androidx.core.view.isVisible
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.tools.decorateView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VisitedProfiler(
        private val visitorState: UserStateViewModel,
        private val userDataState: UserDataViewModel,
        private val subscribeButton: TextView
) : UserProfiler(visitorState, userDataState) {
    private val api = UserAPI()

    fun handleSubscriptionView(followersView: TextView): VisitedProfiler {
        checkFollowerState()

        subscribeButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val action = subscribeButton.text.toString()
                val (st, res) = subscriptionsRequest(action)
                if (Statuses.OK.eqNot(st)) return@launch
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

    fun checkFollowerState(): VisitedProfiler {
        val follower = userDataState.liveData.value!!.follower
        decorateSubscriptionView(follower)
        return this
    }

    private fun decorateSubscriptionView(follower: Boolean) {
        val isSigned = visitorState.namesState.value!!.first != null
        subscribeButton.isVisible = isSigned
        subscribeButton.text = if (follower && isSigned) "unsubscribe" else "subscribe"
        val btnTextColor = if (follower && isSigned) AppColors.PURPLE else AppColors.GREEN
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