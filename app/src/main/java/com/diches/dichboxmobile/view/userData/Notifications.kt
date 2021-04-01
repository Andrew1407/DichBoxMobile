package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.notifications.NotificationsHandler

class Notifications : Fragment() {
    private lateinit var ntsHandler: NotificationsHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val username = userViewModel.liveData.value!!.name
        val cleanBtn = view.findViewById<Button>(R.id.cleanNotificationsList)
        val listView = view.findViewById<ListView>(R.id.notificationsList)
        listView.emptyView = view.findViewById<TextView>(R.id.ntsEmpty)

        ntsHandler = NotificationsHandler(username, listView, userViewModel)
                .handleCleanAction(cleanBtn)
                .createListAdapter(requireContext(), savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        ntsHandler.saveNotificationsState(outState)
    }
}