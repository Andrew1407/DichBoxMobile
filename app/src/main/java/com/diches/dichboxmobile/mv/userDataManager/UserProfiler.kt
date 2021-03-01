package com.diches.dichboxmobile.mv.userDataManager

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.runBlocking

class UserProfiler {
    private val api = UserAPI()
    private lateinit var userData: UserContainer.UserData

    fun getUserData(ctx: Context, bundle: Bundle?): UserContainer.UserData {
        if (bundle != null) {
            val dataStr = bundle.getString("userJSON")
            userData = UserContainer.parseJSON(
                    dataStr!!, UserContainer.UserData::class.java
            ) as UserContainer.UserData
        } else {
            ctx.openFileInput("signed.txt").use { stream ->
                val name = stream?.bufferedReader().use { it?.readText() }
                runBlocking { userData = fetchData(name!!) }
            }
        }
        return userData.copy()
    }

    fun saveUserDataState(bundle: Bundle) {
        val dataStr = UserContainer.stringifyJSON(userData)
        bundle.putString("userJSON", dataStr)
    }

    private suspend fun fetchData(name: String): UserContainer.UserData {
        val resData = UserContainer.FindContainer(name, name)
        val (st, data) = api.findUser(resData)
        userData = data as UserContainer.UserData
        return userData.copy()
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
        val color = -0xff00b4
        decorateView(date, prefix, Pair(userData.reg_date, color))
        return this
    }

    fun fillFollowers(followers: TextView): UserProfiler {
        val prefix = "followers: "
        val color = -0x7f7f80
        decorateView(followers, prefix, Pair(userData.followers.toString(), color))
        return this
    }

    fun fillEmail(email: TextView): UserProfiler {
        if (userData.email == null) {
            email.visibility = View.GONE
            return this
        }
        val prefix = "email: "
        val color = -0x45ff17
        email.visibility = View.VISIBLE
        decorateView(email, prefix, Pair(userData.email!!, color))
        return this
    }

    fun fillLogo(img: ImageView): UserProfiler {
        if (userData.logo != null) {
            val basePrefix = Regex("""^data:image\/png;base64,""")
            val logoSrc = userData.logo!!.replace(basePrefix, "")
            val imageBytes = Base64.decode(logoSrc, Base64.DEFAULT)
            val decoded = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            img.setImageBitmap(decoded)
        }
        return this
    }
}