package com.diches.dichboxmobile.mv.userDataManager

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.tools.fromBase64ToBitmap

class UserProfiler {
    private lateinit var userData: UserContainer.UserData

    fun refreshData(data: UserContainer.UserData) {
        userData = data
    }

    fun setUserData(viewModel: UserDataViewModel, bundle: Bundle?) {
        userData = if (bundle != null)
            UserContainer.parseJSON(
                bundle.getString("userJSON")!!,
                UserContainer.UserData::class.java
            ) as UserContainer.UserData
        else
            viewModel.liveData.value!!.copy()
    }

    fun saveUserDataState(bundle: Bundle) {
        val dataStr = UserContainer.stringifyJSON(userData)
        bundle.putString("userJSON", dataStr)
    }

    private fun fillView(element: TextView, parameters: Pair<String, String>) {
        val (text, color) = parameters
        element.text = text
        element.setTextColor(Color.parseColor(color))
    }

    private fun decorateView(
            element: TextView,
            prefix: String,
            parameters: Pair<String, Int>
    ) {
        val (value, color) = parameters
        val text = prefix + value
        val spannable = SpannableString(text)
        spannable.setSpan(
                ForegroundColorSpan(color),
                prefix.length, text.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        element.text = spannable
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