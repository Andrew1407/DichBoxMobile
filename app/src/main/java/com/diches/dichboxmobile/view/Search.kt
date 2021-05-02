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
import com.diches.dichboxmobile.FragmentsRedirector
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.mv.usersSearch.UsersSearch
import com.diches.dichboxmobile.mv.usersSearch.UsersSearchViewModel

class Search : Fragment() {
    private lateinit var userSearch: UsersSearch
    private lateinit var redirector: FragmentsRedirector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchMsg = view.findViewById<TextView>(R.id.userSearchPrompt)
        val inputField = view.findViewById<EditText>(R.id.userSearch)
        val usersList = view.findViewById<ListView>(R.id.usersList)

        redirector = requireActivity() as FragmentsRedirector

        val visitorViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val usersSearchViewModel = ViewModelProvider(requireActivity()).get(UsersSearchViewModel::class.java)
        val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
        val boxDetailsViewModel = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)
        val filesListViewModel = ViewModelProvider(requireActivity()).get(FilesListViewModel::class.java)
        val openedFilesViewModel = ViewModelProvider(requireActivity()).get(OpenedFilesViewModel::class.java)
        val adaptedViews = listOf(boxViewModel, boxDetailsViewModel, filesListViewModel, openedFilesViewModel)

        usersList.emptyView = searchMsg
        userSearch = UsersSearch(usersList, visitorViewModel, usersSearchViewModel, savedInstanceState != null)
                .createListAdapter(view, savedInstanceState, adaptedViews, redirector)
                .addOnEmptyMsg(searchMsg)
                .handleInputSearch(inputField)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        userSearch.saveFoundState()
    }
}