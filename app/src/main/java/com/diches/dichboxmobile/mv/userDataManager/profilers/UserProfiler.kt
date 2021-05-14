package com.diches.dichboxmobile.mv.userDataManager.profilers

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.tools.decorateView
import com.diches.dichboxmobile.tools.fillView
import com.diches.dichboxmobile.tools.fromBase64ToBitmap
import kotlinx.coroutines.*

open class UserProfiler(
    private val userStateVM: UserStateViewModel,
    private val userDataVM: UserDataViewModel
) {
    private val api = UserAPI()
    private lateinit var userData: UserContainer.UserData

    fun refetchData(
        activity: Activity,
        refreshLayout: SwipeRefreshLayout
    ) {
        val exists = activity.getFileStreamPath("signed.txt")!!.exists()
        if (!exists) return
        activity.openFileInput("signed.txt").use { stream ->
            val uuid = stream?.bufferedReader().use { it?.readText() }!!.reversed()
            CoroutineScope(Dispatchers.Main).launch {
                val container = UserContainer.SignedContainer(uuid = uuid)
                val (st1, res) = withContext(Dispatchers.IO) { api.getUsernameByUuid(container) }
                if (Statuses.OK.eqNot(st1)) {
                    refreshLayout.isRefreshing = false
                    return@launch
                }
                val (name, _) = (res as UserContainer.SignedContainer)
                userStateVM.setState(Pair(name, name))
                val names = userStateVM.namesState.value!!
                val resData = UserContainer.FindContainer(
                    username = names.first,
                    pathName = names.second!!
                )
                val (st2, data) = withContext(Dispatchers.IO) { api.findUser(resData) }
                if (Statuses.OK.eqNot(st2)) {
                    refreshLayout.isRefreshing = false
                    return@launch
                }
                val userData = data as UserContainer.UserData
                userDataVM.setUserData(userData)
                refreshData(userData)
                refreshLayout.isRefreshing = false
            }
        }
    }

    fun refreshData(data: UserContainer.UserData) {
        userData = data
    }

    fun setUserData(): UserProfiler {
        userData = userDataVM.liveData.value!!.copy()
        return this
    }

    fun fillUsername(name: TextView): UserProfiler {
        fillView(name, Pair(userData.name, userData.name_color))
        return this
    }

    fun fillDescription(desc: TextView): UserProfiler {
        if (userData.description.isEmpty()) {
            desc.visibility = View.GONE
        } else {
            desc.visibility = View.VISIBLE
            fillView(desc, Pair(userData.description, userData.description_color))
        }
        return this
    }

    fun fillDate(date: TextView): UserProfiler {
        val prefix = "signed: "
        val color = AppColors.GREEN.raw
        decorateView(date, prefix, Pair(userData.reg_date, color))
        return this
    }

    fun fillFollowers(followers: TextView): UserProfiler {
        val prefix = "followers: "
        val color = AppColors.GRAY.raw
        decorateView(followers, prefix, Pair(userData.followers.toString(), color))
        return this
    }

    fun fillEmail(email: TextView): UserProfiler {
        if (userData.email == null) {
            email.visibility = View.GONE
            return this
        }
        val prefix = "email: "
        val color = AppColors.PURPLE.raw
        email.visibility = View.VISIBLE
        decorateView(email, prefix, Pair(userData.email!!, color))
        return this
    }

    fun fillLogo(img: ImageView): UserProfiler {
        if (userData.logo != null) {
            val decoded = fromBase64ToBitmap(userData.logo!!)
            img.setImageBitmap(decoded)
        } else {
            img.setImageResource(R.drawable.default_user_logo)
        }
        return this
    }
}