package com.diches.dichboxmobile.mv.userDataManager.subscriptions

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionsAdapter(
        context: Context,
        private val resource: Int,
        private val items: MutableList<UserContainer.FoundUser>,
        private var itemsShown: MutableList<UserContainer.FoundUser>,
        private val onRemoveSub: suspend (name: String) -> Boolean
) : ArrayAdapter<UserContainer.FoundUser>(context, resource, items), Filterable {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView ?: LayoutInflater
            .from(context)
            .inflate(resource, parent, false)

        val logo = row.findViewById<ImageView>(R.id.subscription_logo)
        val name = row.findViewById<TextView>(R.id.subscription_name)
        val rmIcon = row.findViewById<ImageView>(R.id.removeSubscription)
        val subscription = itemsShown[position]

        setSubscriptionIcon(logo, subscription.logo)
        handleUnsubscribe(rmIcon, subscription.name)
        name.text = subscription.name
        name.setTextColor(Color.parseColor(subscription.name_color))
        return row
    }

    override fun getCount(): Int = itemsShown.size
    override fun getItem(position: Int): UserContainer.FoundUser = itemsShown[position]
    override fun getItemId(position: Int): Long = position.toLong()

    fun getItems(): Pair<List<UserContainer.FoundUser>, List<UserContainer.FoundUser>> {
        return Pair(items, itemsShown)
    }

    private fun setSubscriptionIcon(logoView: ImageView, logoSrc: String?) {
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

    private fun handleUnsubscribe(btn: ImageView, subscription: String) {
        btn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val removed = withContext(Dispatchers.IO){ onRemoveSub(subscription) }
                if (!removed) return@launch
                items.removeIf { it.name == subscription }
                itemsShown.removeIf { it.name == subscription }
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemsShown = results!!.values as MutableList<UserContainer.FoundUser>
            notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val input = constraint.toString()
            val filterResults = FilterResults()
            filterResults.values = items.filter { input in it.name }
            return filterResults
        }
    }
}