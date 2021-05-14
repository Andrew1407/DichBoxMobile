package com.diches.dichboxmobile.mv.userDataManager.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.tools.fromBase64ToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationsAdapter(
    context: Context,
    private val resource: Int,
    var items: MutableList<UserContainer.NotificationData>,
    private val onRemoveNotification: suspend (id: Int) -> Unit
) : ArrayAdapter<UserContainer.NotificationData>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val row = convertView ?: LayoutInflater
                .from(context)
                .inflate(resource, parent, false)

        val msg = row.findViewById<TextView>(R.id.notification_msg)
        val logo = row.findViewById<ImageView>(R.id.notification_logo)
        val time = row.findViewById<TextView>(R.id.notification_date)
        val rmIcon = row.findViewById<ImageView>(R.id.notificationsRmIcon)

        val notification = items[position]
        setNoteIcon(logo, notification.icon)
        handleNotificationRemoval(rmIcon, notification.id)
        time.text = notification.note_date

        val msgType = NotificationsTypes.defineType(notification.type)
        msgType.decorateView(notification, msg)

        return row
    }

    override fun getCount(): Int = items.size

    private fun setNoteIcon(logoView: ImageView, logoSrc: String?) {
        if (logoSrc != null) {
            val decoded = fromBase64ToBitmap(logoSrc)
            logoView.setImageBitmap(decoded)
        } else {
            logoView.setImageResource(R.drawable.default_user_logo)
        }
    }

    private fun handleNotificationRemoval(btn: ImageView, id: Int) {
        btn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch { onRemoveNotification(id) }
        }
    }
}
