package com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.AccessListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccessList(
        containerView: View,
        private val listsState: AccessListViewModel,
        private val keyPrefix: String,
        defaultFound: List<UserContainer.FoundUser> = emptyList()
) {
    private val api = UserAPI()
    private val searchInput = containerView.findViewById<EditText>(R.id.namesSearch)
    private val clearInputBtn = containerView.findViewById<Button>(R.id.namesCleanBtn)
    private val foundUsersView =  containerView.findViewById<ListView>(R.id.foundUsers)
    private val addedUsersView = containerView.findViewById<ListView>(R.id.addedUsers)
    private val foundUsers: List<UserContainer.FoundUser> = listsState.found.value ?: defaultFound
    private val addedUsers: List<UserContainer.FoundUser> = listsState.added.value ?: emptyList()
    private var savedInput = ""

    init {
        if (addedUsers.isEmpty())
            foundUsersView.visibility = View.GONE
    }

    fun setSavedInput(bundle: Bundle?): AccessList {
        savedInput = bundle?.getString(keyPrefix + "Input") ?: ""
        return this
    }

    fun saveState(bundle: Bundle) {
        val found = (foundUsersView.adapter as FoundUsersAdapter).items
        val added = (addedUsersView.adapter as AddedUsersAdapter).items
        listsState.setFoundUsers(found)
        listsState.setAddedUsers(added)
        bundle.putString(keyPrefix + "Input", searchInput.text.toString())
    }

    fun addListAdapters(ctx: Context): AccessList {
        addedUsersView.adapter = AddedUsersAdapter(ctx, R.layout.found_user, addedUsers.toMutableList())
        foundUsersView.adapter = FoundUsersAdapter(ctx, R.layout.found_user, foundUsers) {
            addFoundUser(it)
        }

        return this
    }

    private fun addFoundUser(user: UserContainer.FoundUser) {
        val addedAdapter = addedUsersView.adapter as AddedUsersAdapter
        val usersIncluded = addedAdapter.items
                .filter { it.name == user.name }
        if (usersIncluded.isNotEmpty()) return
        addedAdapter.items.add(user)
        addedAdapter.notifyDataSetChanged()
        searchInput.text.clear()
    }

    fun handleSearch(username: String): AccessList {
        searchInput.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val input = searchInput.text.toString()
                val foundAdapter = foundUsersView.adapter as FoundUsersAdapter
                if (input.isEmpty()) {
                    foundAdapter.items = emptyList()
                    if (foundUsersView.isVisible)
                        foundUsersView.visibility = View.GONE
                } else {
                    val bodyContainer = UserContainer.NameListSearch(username, input)
                    val (st, res) = withContext(Dispatchers.IO) { api.getUsernames(bodyContainer) }
                    val (foundUsers) = res as UserContainer.FoundNamesList
                    foundAdapter.items = foundUsers
                    foundUsersView.visibility = if (foundUsers.isEmpty()) View.GONE else View.VISIBLE
                }
                foundAdapter.notifyDataSetChanged()
            }
        }

        if (savedInput.isNotEmpty())
            searchInput.text = Editable.Factory.getInstance().newEditable(savedInput)

        clearInputBtn.setOnClickListener { searchInput.text.clear() }
        return this
    }

    fun getAddedUsers(): List<UserContainer.FoundUser> {
        val addedAdapter = (addedUsersView.adapter as AddedUsersAdapter)
        return addedAdapter.items.toList()
    }
}