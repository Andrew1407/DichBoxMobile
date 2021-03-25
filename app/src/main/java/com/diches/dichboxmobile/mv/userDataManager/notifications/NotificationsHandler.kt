package com.diches.dichboxmobile.mv.userDataManager.notifications

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.*

class NotificationsHandler(
        private val username: String,
        private val listView: ListView
) {
    private val api = UserAPI()
    private lateinit var cleanBtn: Button

    fun createListAdapter(ctx: Context, bundle: Bundle?): NotificationsHandler {
        val nts = if (bundle == null) getNotificationsByRequest()
            else parseSavedNotifications(bundle)
        listView.adapter = NotificationsAdapter(ctx, R.layout.notification, nts) {
            removeNotifications(listOf(it))
        }
        onEmptyListCheck()
        return this
    }

    private fun getNotificationsByRequest(): MutableList<UserContainer.NotificationData> {
        val reqContainer = UserContainer.NameContainer(username)
        val (st, res) = runBlocking { api.getNotifications(reqContainer) }
        val (notifications) = res as UserContainer.Notifications
        return notifications.toMutableList()
    }

    private fun parseSavedNotifications(bundle: Bundle): MutableList<UserContainer.NotificationData> {
        val ntsStr = bundle.getString("notifications")!!
        val parsed = UserContainer.parseJSON(ntsStr, UserContainer.Notifications::class.java)
        val (notifications) = parsed as UserContainer.Notifications
        return notifications.toMutableList()
    }

    fun saveNotificationsState(bundle: Bundle) {
        val notificationsList = (listView.adapter as NotificationsAdapter).getItems()
        val ntsWrapped = UserContainer.Notifications(notificationsList)
        val ntsStr = UserContainer.stringifyJSON(ntsWrapped)
        bundle.putString("notifications", ntsStr)
    }

    private suspend fun removeNotifications(ids: List<Int>) {
        val ntsItems = (listView.adapter as NotificationsAdapter).getItems()
        val removed = withContext(Dispatchers.IO) {
            val rmContainer = UserContainer.NotificationsRemoved(username, ids)
            val (st, res) = api.removeNotifications(rmContainer)
            val (removed) = res as UserContainer.RemovedRes
            removed && st == 200
        }

        if (!removed) return
        ntsItems.removeIf { ids.indexOf(it.id) != -1 }
        (listView.adapter as NotificationsAdapter).notifyDataSetChanged()
        onEmptyListCheck()
    }

    private fun onEmptyListCheck() {
        val isEmpty = listView.adapter.count == 0
        cleanBtn.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    fun handleCleanAction(btn: Button): NotificationsHandler {
        cleanBtn = btn
        cleanBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val ntsItems = (listView.adapter as NotificationsAdapter).getItems()
                val ntsIds = ntsItems.map { it.id }
                removeNotifications(ntsIds)
            }
        }
        return this
    }
}