package com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.tools.fromBase64ToBitmap

class FoundUsersAdapter (
        context: Context,
        private val resource: Int,
        var items: List<UserContainer.FoundUser>,
        private val addUserClb: (user: UserContainer.FoundUser) -> Unit
) : ArrayAdapter<UserContainer.FoundUser>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView ?: LayoutInflater
                .from(context)
                .inflate(resource, parent, false)

        val rmIcon = row.findViewById<ImageView>(R.id.removeSubscription)
        rmIcon.visibility = View.GONE

        val logo = row.findViewById<ImageView>(R.id.subscription_logo)
        val name = row.findViewById<TextView>(R.id.subscription_name)
        val user = items[position]
        chooseUser(row, user)
        fillIcon(logo, user.logo)
        name.text = user.name
        name.setTextColor(Color.parseColor(user.name_color))

        return row
    }

    private fun chooseUser(view: View, user: UserContainer.FoundUser) {
        view.setOnClickListener { addUserClb(user) }
    }

    private fun fillIcon(logoView: ImageView, logoSrc: String?) {
        if (logoSrc != null) {
            val decoded = fromBase64ToBitmap(logoSrc)
            logoView.setImageBitmap(decoded)
        } else {
            logoView.setImageResource(R.drawable.default_user_logo)
        }
    }

    override fun getCount(): Int = items.size
}
