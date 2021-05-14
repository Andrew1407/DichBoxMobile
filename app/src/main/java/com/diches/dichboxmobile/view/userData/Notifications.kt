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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.notifications.NotificationsHandler
import com.diches.dichboxmobile.mv.userDataManager.notifications.NotificationsViewModel

class Notifications : Fragment() {
    private lateinit var ntsHandler: NotificationsHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val notificationsViewModel = ViewModelProvider(requireActivity()).get(NotificationsViewModel::class.java)
        val username = userViewModel.liveData.value!!.name
        val cleanBtn = view.findViewById<Button>(R.id.cleanNotificationsList)
        val listView = view.findViewById<ListView>(R.id.notificationsList)
        listView.emptyView = view.findViewById<TextView>(R.id.ntsEmpty)

        ntsHandler = NotificationsHandler(username, listView, userViewModel, notificationsViewModel)
                .handleCleanAction(cleanBtn)
                .createListAdapter(requireContext(), savedInstanceState)

        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refreshNotifications)
        var userData = userViewModel.liveData.value!!
        refreshLayout.setOnRefreshListener {
            ntsHandler.refreshNotifications()
            refreshLayout.isRefreshing = false
            userData = userViewModel.liveData.value!!
        }

        userViewModel.liveData.observe(viewLifecycleOwner) {
            if (it == null || it == userData) return@observe
            ntsHandler.refreshNotifications()
            userData = it
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        ntsHandler.saveNotificationsState()
    }
}