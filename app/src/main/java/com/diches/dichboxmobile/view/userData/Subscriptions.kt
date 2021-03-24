package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.subscriptions.SubscriptionsHandler
import com.diches.dichboxmobile.mv.userDataManager.UserDataViewModel

class Subscriptions : Fragment() {
    private lateinit var subsHandler: SubscriptionsHandler

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_subscriptions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val username = userViewModel.liveData.value!!.name

        val listView = view.findViewById<ListView>(R.id.subscriptionsList)
        listView.emptyView = view.findViewById<TextView>(R.id.emptySubs)

        val search = view.findViewById<EditText>(R.id.subscriptionsSearch)

        subsHandler = SubscriptionsHandler(username, listView)
                .createListAdapter(requireContext(), savedInstanceState)
                .handleSearch(search)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        subsHandler.saveSubsList(outState)
    }
}