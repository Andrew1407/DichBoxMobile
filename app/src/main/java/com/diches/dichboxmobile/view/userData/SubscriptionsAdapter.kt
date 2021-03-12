package com.diches.dichboxmobile.view.userData

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.NotificationContainer
import com.diches.dichboxmobile.mv.userDataManager.SubscriptionContainer

class SubscriptionsAdapter(context: Context, private val resource: Int, private val items: List<SubscriptionContainer>
) : ArrayAdapter<SubscriptionContainer>(context, resource, items) {
    private lateinit var row: View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        row = convertView ?: LayoutInflater
            .from(context)
            .inflate(resource, parent, false)
        val logo = row.findViewById<ImageView>(R.id.subscription_logo)
        val name = row.findViewById<TextView>(R.id.subscription_name)

        val subscription = items[position]
        if (subscription.logo === null)
            logo.setImageResource(R.drawable.default_user_logo)
        name.text = subscription.username
        name.setTextColor(subscription.color)
        return row
    }
}