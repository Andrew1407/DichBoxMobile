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

class AddedUsersAdapter(
        context: Context,
        private val resource: Int,
        var items: MutableList<UserContainer.FoundUser>,
        private val okClb: (() -> Unit)? = null
) : ArrayAdapter<UserContainer.FoundUser>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView ?: LayoutInflater
                .from(context)
                .inflate(resource, parent, false)

        val rmIcon = row.findViewById<ImageView>(R.id.removeSubscription)
        val logo = row.findViewById<ImageView>(R.id.subscription_logo)
        val name = row.findViewById<TextView>(R.id.subscription_name)
        val user = items[position]
        handleRemove(rmIcon, user.name)
        fillIcon(logo, user.logo)
        name.text = user.name
        name.setTextColor(Color.parseColor(user.name_color))

        return row
    }

    private fun fillIcon(logoView: ImageView, logoSrc: String?) {
        if (logoSrc != null) {
            val decoded = fromBase64ToBitmap(logoSrc)
            logoView.setImageBitmap(decoded)
        } else {
            logoView.setImageResource(R.drawable.default_user_logo)
        }
    }

    private fun handleRemove(icon: ImageView, name: String) {
        icon.setImageResource(R.drawable.trash_bin_round)
        icon.setOnClickListener {
            items.removeIf { it.name == name }
            notifyDataSetChanged()
            okClb?.invoke()
        }
    }

    override fun getCount(): Int = items.size
}