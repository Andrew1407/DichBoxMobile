package com.diches.dichboxmobile.view.userData

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.NotificationContainer
import com.diches.dichboxmobile.mv.userDataManager.SubscriptionContainer

class Subscriptions : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_subscriptions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.subscriptionsList)
        val items = listOf(
            SubscriptionContainer(null, "olegator", Color.parseColor("#03c9ff")),
            SubscriptionContainer(null, "VlaD", Color.parseColor("#0cd452"))
        )

        listView.adapter = SubscriptionsAdapter(requireContext(), R.layout.subscription, items)
    }
}