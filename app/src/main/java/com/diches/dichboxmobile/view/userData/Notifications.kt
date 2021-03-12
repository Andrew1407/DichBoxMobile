package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.NotificationContainer

class Notifications : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.notificationsList)
        val items = listOf(
            NotificationContainer(null, "03.02.2021, 02:22:12 PM", "User VlaD has created a new box: RSO"),
            NotificationContainer(null, "03.01.2021, 11:42:32 PM", "The account you followed bobo has been removed"),
            NotificationContainer(null, "02.27.2021, 10:47:23 PM", "Welcome to \"DichBox\" world. You need to know nothing, just start creating boxes, editing your profile, searching other users etc. Good luck!")
        )

        listView.adapter = NotificationsAdapter(requireContext(), R.layout.notification, items)
    }
}