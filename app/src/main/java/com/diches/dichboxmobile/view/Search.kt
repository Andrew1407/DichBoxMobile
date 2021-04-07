package com.diches.dichboxmobile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.BoxesListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.mv.usersSearch.UsersSearch
import com.diches.dichboxmobile.mv.usersSearch.UsersSearchViewModel

class Search : Fragment() {
    private lateinit var userSearch: UsersSearch
    private lateinit var redirector: Redirector

    interface Redirector {
        fun handleRedirection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchMsg = view.findViewById<TextView>(R.id.userSearchPrompt)
        val inputField = view.findViewById<EditText>(R.id.userSearch)
        val usersList = view.findViewById<ListView>(R.id.usersList)
        redirector = requireActivity() as Redirector
        val visitorViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
        val usersSearchViewModel = ViewModelProvider(requireActivity()).get(UsersSearchViewModel::class.java)
        usersList.emptyView = searchMsg
        userSearch = UsersSearch(usersList, visitorViewModel, usersSearchViewModel, savedInstanceState != null)
                .createListAdapter(view, savedInstanceState, boxViewModel, redirector)
                .addOnEmptyMsg(searchMsg)
                .handleInputSearch(inputField)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        userSearch.saveFoundState()
    }
}