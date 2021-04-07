package com.diches.dichboxmobile.mv.verifiers.editVerifiers.box

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.view.isVisible
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.AccessListViewModel
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList.AccessList

class BoxPrivacyHandler(
        privacyView: View,
        bundle: Bundle?,
        listsState: AccessListViewModel,
        initialPrivacy: String,
        initialLimitedList: List<UserContainer.FoundUser> = emptyList()
) {
    private val limitedListView = privacyView.findViewById<LinearLayout>(R.id.limitedContainer)
    private val limitedContainer = AccessList(limitedListView, listsState, "privacy", initialLimitedList)
            .setSavedInput(bundle)
    private var privacy: String = if (bundle != null) bundle.getString("privacy")!! else initialPrivacy
    private val radios: List<RadioButton>

    init {
        limitedListView.isVisible = privacy == "limited"
        radios = listOf(
                R.id.radioPublic, R.id.radioPrivate,
                R.id.radioFollowers, R.id.radioLimited
        ).map { privacyView.findViewById(it) }

        if (bundle == null) radios.forEach {
            val radioDescription = it.text.toString()
            if (radioDescription.startsWith(privacy)) it.isChecked = true
        }
    }

    fun handleLimitedView(ctx: Context, username: String): BoxPrivacyHandler {
        limitedContainer
                .addListAdapters(ctx)
                .handleSearch(username)

        return this
    }

    fun handleRadioChoice(): BoxPrivacyHandler {
        radios.forEach {
            it.setOnClickListener { _ ->
                privacy = it.text.toString().split(" ")[0]
                if (privacy == "limited") limitedListView.visibility = View.VISIBLE
                else if (limitedListView.isVisible) limitedListView.visibility = View.GONE
            }
        }


        return this
    }

    fun saveState(bundle: Bundle) {
        limitedContainer.saveState(bundle)
        bundle.putString("privacy", privacy)
    }

    fun getPrivacy(): Pair<String, List<UserContainer.FoundUser>> = Pair(privacy, limitedContainer.getAddedUsers())
}