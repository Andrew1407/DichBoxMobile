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

class NotificationsAdapter(context: Context, private val resource: Int, private val items: List<NotificationContainer>
) : ArrayAdapter<NotificationContainer>(context, resource, items) {
    private lateinit var row: View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        row = convertView ?: LayoutInflater
                .from(context)
                .inflate(resource, parent, false)
        val msg = row.findViewById<TextView>(R.id.notification_msg)
        val logo = row.findViewById<ImageView>(R.id.notification_logo)
        val time = row.findViewById<TextView>(R.id.notification_date)

        val notification = items[position]
        if (notification.logo === null)
            logo.setImageResource(R.drawable.default_user_logo)
        msg.text = notification.msg
        time.text = notification.time
        return row
    }
}