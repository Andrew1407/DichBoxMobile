package com.diches.dichboxmobile.mv.userDataManager.notifications

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import kotlinx.coroutines.*

class NotificationsHandler(
        private val username: String,
        private val listView: ListView,
        private val userViewModel: UserDataViewModel,
        private val notificationsViewModel: NotificationsViewModel
) {
    private val api = UserAPI()
    private lateinit var cleanBtn: Button

    fun createListAdapter(ctx: Context, bundle: Bundle?): NotificationsHandler {
        val nts = if (bundle == null) getNotificationsByRequest()
            else notificationsViewModel.liveData.value!!.toMutableList()
        listView.adapter = NotificationsAdapter(ctx, R.layout.notification, nts) {
            removeNotifications(listOf(it))
        }
        onEmptyListCheck()
        return this
    }

    fun refreshNotifications() {
        val nts = getNotificationsByRequest()
        val adapter = listView.adapter as NotificationsAdapter
        adapter.items = nts
        refreshNotificationsAmount(nts.size)
        adapter.notifyDataSetChanged()
        onEmptyListCheck()
    }

    private fun getNotificationsByRequest(): MutableList<UserContainer.NotificationData> {
        val reqContainer = UserContainer.SignedContainer(username)
        val (_, res) = runBlocking { api.getNotifications(reqContainer) }
        val (notifications) = res as UserContainer.Notifications
        return notifications.toMutableList()
    }

    fun saveNotificationsState() {
        val notificationsList = (listView.adapter as NotificationsAdapter).items
        notificationsViewModel.setNotifications(notificationsList)
    }

    private suspend fun removeNotifications(ids: List<Int>) {
        val ntsItems = (listView.adapter as NotificationsAdapter).items
        val removed = withContext(Dispatchers.IO) {
            val rmContainer = UserContainer.NotificationsRemoved(username, ids)
            val (st, res) = api.removeNotifications(rmContainer)
            val (removed) = res as UserContainer.RemovedRes
            removed && st == 200
        }

        if (!removed) return
        ntsItems.removeIf { ids.indexOf(it.id) != -1 }
        (listView.adapter as NotificationsAdapter).notifyDataSetChanged()
        refreshNotificationsAmount(ntsItems.size)
        onEmptyListCheck()
    }

    private fun refreshNotificationsAmount(size: Int) {
        val userState = userViewModel.liveData.value!!
        userViewModel.setUserData(userState.copy(notifications = size))
    }

    private fun onEmptyListCheck() {
        val isEmpty = listView.adapter.count == 0
        cleanBtn.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    fun handleCleanAction(btn: Button): NotificationsHandler {
        cleanBtn = btn
        cleanBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val ntsItems = (listView.adapter as NotificationsAdapter).items
                val ntsIds = ntsItems.map { it.id }
                removeNotifications(ntsIds)
            }
        }
        return this
    }
}