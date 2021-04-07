package com.diches.dichboxmobile.mv.userDataManager.subscriptions

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Filterable
import android.widget.ListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import kotlinx.coroutines.runBlocking

class SubscriptionsHandler(
        private val username: String,
        private val listView: ListView,
        private val subscriptionsState: SubscriptionsViewModel
) {
    private val api = UserAPI()

    fun createListAdapter(ctx: Context, bundle: Bundle?): SubscriptionsHandler {
        val items: List<UserContainer.FoundUser>
        val itemsShown: List<UserContainer.FoundUser>

        if (bundle == null) {
            items = getSubsByRequest().toMutableList()
            itemsShown = items.toMutableList()
        } else {
            val (i, ish) = subscriptionsState.liveData.value!!
            items = i.toMutableList()
            itemsShown = ish.toMutableList()
        }

        listView.adapter = SubscriptionsAdapter(ctx, R.layout.found_user, items, itemsShown) {
            unsubscribe(it)
        }

        return this
    }

    fun handleSearch(search: EditText): SubscriptionsHandler {
        search.addTextChangedListener {
            (listView.adapter as Filterable).filter.filter(search.text)
        }
        return this
    }

    private fun getSubsByRequest(): List<UserContainer.FoundUser> {
        val requestContainer = UserContainer.NameContainer(username)
        val (st, res) = runBlocking { api.getSubscriptions(requestContainer) }
        val (subs) = (res as UserContainer.Subscriptions)
        return subs
    }

    fun saveSubsList() {
        val subs = (listView.adapter as SubscriptionsAdapter).getItems()
        subscriptionsState.setSubs(subs)
    }

    private suspend fun unsubscribe(name: String): Boolean {
        val subContainer = UserContainer.SubsAction(
                action = "unsubscribe",
                personName = username,
                subscriptionName = name
        )

        val (st, res) = api.subscribeAction(subContainer)
        val unsubscribed = (res as UserContainer.SubsActionRes).unsubscribed
        return st == 200 && unsubscribed
    }
}