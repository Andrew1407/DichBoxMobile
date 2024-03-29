package com.diches.dichboxmobile.mv.usersSearch

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.FragmentsRedirector
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.Cleanable
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersSearch(
        private val listView: ListView,
        private val userStateViewModel: UserStateViewModel,
        private val usersSearchViewModel: UsersSearchViewModel,
        private var rotated: Boolean
) {
    private val api = UserAPI()
    private lateinit var onEmptyMsg: TextView
    private lateinit var searchInput: EditText

    private suspend fun searchUsers(chunk: String): List<UserContainer.FoundUser> {
        val chunkContainer = UserContainer.SearchedChunk(chunk)
        val (st, res) = withContext(Dispatchers.IO) { api.search(chunkContainer) }
        val (searched) = res as UserContainer.FoundUsers
        return searched
    }

    fun createListAdapter(
            view: View,
            bundle: Bundle?,
            viewStates: List<Cleanable>,
            redirector: FragmentsRedirector): UsersSearch {
        val initialList = if (bundle == null) emptyList() else usersSearchViewModel.liveData.value!!
        listView.adapter = UsersSearchAdapter(view.context, R.layout.found_user, initialList) {
            visitUserPage(it, redirector)
            redirectionCleanup(view, viewStates)
        }
        return this
    }

    private fun redirectionCleanup(view: View, viewStates: List<Cleanable>) {
        viewStates.forEach { it.clear() }
        usersSearchViewModel.setUsers(emptyList())
        searchInput.text.clear()
        ContextCompat
                .getSystemService(view.context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun visitUserPage(name: String, redirector: FragmentsRedirector) {
        val oldState = userStateViewModel.namesState.value!!
        userStateViewModel.setState(oldState.copy(second = name))
        redirector.redirectSearched()
    }

    fun saveFoundState() {
        val users = (listView.adapter as UsersSearchAdapter).items
        usersSearchViewModel.setUsers(users)
    }

    fun handleInputSearch(inputField: EditText): UsersSearch {
        searchInput = inputField
        searchInput.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val inputChunk = inputField.text.toString()
                val chunkIsEmpty = inputChunk.isEmpty()

                if (rotated) {
                    val users = (listView.adapter as UsersSearchAdapter).items
                    handleOnEmptyMsg(users, chunkIsEmpty)
                    rotated = false
                    return@launch
                }

                val usersList = if (chunkIsEmpty) emptyList() else searchUsers(inputChunk)
                handleOnEmptyMsg(usersList, chunkIsEmpty)
                (listView.adapter as UsersSearchAdapter).items = usersList
                (listView.adapter as UsersSearchAdapter).notifyDataSetChanged()
            }
        }

        return this
    }

    private fun handleOnEmptyMsg(list: List<UserContainer.FoundUser>, chunkIsEmpty: Boolean) {
        if (list.isEmpty())
            onEmptyMsg.text = if (chunkIsEmpty) "Search here for users"  else "No users were found"
    }

    fun addOnEmptyMsg(msgText: TextView): UsersSearch {
        onEmptyMsg = msgText
        return this
    }
}