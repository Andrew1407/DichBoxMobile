package com.diches.dichboxmobile.mv.userDataManager.notifications

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsAdapter(
    context: Context,
    private val resource: Int,
    private val items: MutableList<UserContainer.NotificationData>,
    private val onRemoveNotification: suspend (id: Int) -> Unit
) : ArrayAdapter<UserContainer.NotificationData>(context, resource, items) {
    private lateinit var row: View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        row = convertView ?: LayoutInflater
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

    fun getItems(): MutableList<UserContainer.NotificationData> = items

    private fun setNoteIcon(logoView: ImageView, logoSrc: String?) {
        if (logoSrc == null) {
            logoView.setImageResource(R.drawable.default_user_logo)
            return
        }
        val basePrefix = Regex("""^data:image\/png;base64,""")
        val logoSrcRaw = logoSrc.replace(basePrefix, "")
        val imageBytes = Base64.decode(logoSrcRaw, Base64.DEFAULT)
        val decoded = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        logoView.setImageBitmap(decoded)
    }

    private fun handleNotificationRemoval(btn: ImageView, id: Int) {
        btn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch { onRemoveNotification(id) }
        }
    }

}