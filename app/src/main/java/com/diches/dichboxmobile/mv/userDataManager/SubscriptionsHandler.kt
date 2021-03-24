package com.diches.dichboxmobile.mv.userDataManager

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Filterable
import android.widget.ListView
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SubscriptionsHandler(
        private val username: String,
        private val listView: ListView
) {
    private val api = UserAPI()

    fun createListAdapter(ctx: Context, bundle: Bundle?): SubscriptionsHandler {
        val items: List<UserContainer.FoundUser>
        val itemsShown: List<UserContainer.FoundUser>

        if (bundle == null) {
            items = getSubsByRequest(ctx).toMutableList()
            itemsShown = items.toMutableList()
        } else {
            val (i, ish) = parseSavedSubs(bundle)
            items = i.toMutableList()
            itemsShown = ish.toMutableList()
        }

        listView.adapter = SubscriptionsAdapter(ctx, R.layout.subscription, items, itemsShown) {
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

    private fun parseSavedSubs(bundle: Bundle): Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>> {
        val parse = { key: String ->
            val subsStr = bundle.getString(key)!!
            val subsContainer = UserContainer.parseJSON(subsStr, UserContainer.Subscriptions::class.java)
            (subsContainer as UserContainer.Subscriptions).subs
        }

        return Pair(parse("items"), parse("itemsShown"))
    }

    private fun getSubsByRequest(ctx: Context): List<UserContainer.FoundUser> {
        val requestContainer = UserContainer.NameContainer(username)
        val (st, res) = runBlocking { api.getSubscriptions(requestContainer) }
        val (subs) = (res as UserContainer.Subscriptions)
        return subs
    }

    fun saveSubsList(bundle: Bundle) {
        val (items, itemsShown) = (listView.adapter as SubscriptionsAdapter).getItems()
        val stringify = { arr: List<UserContainer.FoundUser> ->
            val container = UserContainer.Subscriptions(arr)
            UserContainer.stringifyJSON(container)
        }

        bundle.putString("items", stringify(items))
        bundle.putString("itemsShown", stringify(itemsShown))
    }

    private suspend fun unsubscribe(name: String): Boolean {
        val subContainer = UserContainer.SubsAction(
                action = "unsubscribe",
                personName = username,
                subscriptionName = name
        )

        val (st, res) = api.subscribeAction(subContainer)
        val (unsubscribed) = res as UserContainer.SubsActionRes
        return (st == 200 && unsubscribed)
    }
}