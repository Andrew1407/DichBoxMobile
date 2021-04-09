package com.diches.dichboxmobile.mv.userDataManager.profilers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.tools.decorateView
import com.diches.dichboxmobile.tools.fillView
import com.diches.dichboxmobile.tools.fromBase64ToBitmap

open class UserProfiler {
    private lateinit var userData: UserContainer.UserData

    fun refreshData(data: UserContainer.UserData) {
        userData = data
    }

    fun setUserData(viewModel: UserDataViewModel) {
        userData = viewModel.liveData.value!!.copy()
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