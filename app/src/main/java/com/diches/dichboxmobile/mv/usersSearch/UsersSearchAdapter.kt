package com.diches.dichboxmobile.mv.usersSearch

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
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

class UsersSearchAdapter(
        context: Context,
        private val resource: Int,
        var items: List<UserContainer.FoundUser>,
        val visitUserClb: (name: String) -> Unit
) : ArrayAdapter<UserContainer.FoundUser>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView ?: LayoutInflater
                .from(context)
                .inflate(resource, parent, false)

        val logo = row.findViewById<ImageView>(R.id.subscription_logo)
        val name = row.findViewById<TextView>(R.id.subscription_name)
        val rmIcon = row.findViewById<ImageView>(R.id.removeSubscription)

        rmIcon.visibility = View.GONE

        val user = items[position]
        onPageVisitHandler(row, user.name)
        setIcon(logo, user.logo)
        name.text = user.name
        name.setTextColor(Color.parseColor(user.name_color))

        return row
    }

    private fun onPageVisitHandler(view: View, name: String) {
        view.setOnClickListener {
            visitUserClb(name)
        }
    }

    private fun setIcon(logoView: ImageView, logoSrc: String?) {
        if (logoSrc != null) {
            val decoded = fromBase64ToBitmap(logoSrc)
            logoView.setImageBitmap(decoded)
        } else {
            logoView.setImageResource(R.drawable.default_user_logo)
        }
    }

    override fun getCount(): Int = items.size
}