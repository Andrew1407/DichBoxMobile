package com.diches.dichboxmobile.mv.userDataManager.notifications

import android.graphics.Color
import android.graphics.Typeface.NORMAL
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import com.diches.dichboxmobile.datatypes.UserContainer

enum class NotificationsTypes {
    VIEWER_ADD {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) = setListNote(data, view)
    },
    VIEWER_RM {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) = setListNote(data, view)
    },
    EDITOR_ADD {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) = setListNote(data, view)
    },
    EDITOR_RM {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) = setListNote(data, view)
    },
    BOX_ADD {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) = setListNote(data, view)
    },
    USER_RM {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) {
            val msgParts = data.msgEntries
            val noteMsg = "${msgParts[0]} (${data.user_name}) ${msgParts[1]}."
            val spannable = SpannableString(noteMsg)
            val spanStart = msgParts[0].length + 2
            val spans = mapOf (
                data.user_color!! to Pair(spanStart, spanStart + data.user_name!!.length),
            )
            fillSpannable(spannable, spans)
            view.text = spannable
        }
    },
    HELLO_MSG {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) {
            view.text = data.msgEntries[0]
        }
    },
    INVALID {
        override fun decorateView(data: UserContainer.NotificationData, view: TextView) {
            view.text = "Invalid notification"
        }
    };

    abstract fun decorateView(data: UserContainer.NotificationData, view: TextView)

    companion object {
        fun defineType(typeStr: String): NotificationsTypes = when(typeStr) {
            "viewerAdd" -> VIEWER_ADD
            "viewerRm" -> VIEWER_RM
            "editorAdd" -> EDITOR_ADD
            "editorRm" -> EDITOR_RM
            "boxAdd" -> BOX_ADD
            "userRm" -> USER_RM
            "helloMsg" -> HELLO_MSG
            else -> INVALID
        }
    }
}

internal fun setListNote(data: UserContainer.NotificationData, view: TextView) {
    val msgParts = data.msgEntries
    val finalPart = if (msgParts.size > 2) " ${msgParts[2]}" else ""
    val noteMsg = "${msgParts[0]} ${data.user_name} ${msgParts[1]} ${data.box_name + finalPart}."
    val spannable = SpannableString(noteMsg)
    val userSpanStart =  msgParts[0].length
    val userSpanEnd = userSpanStart + data.user_name!!.length + 1
    val boxSpanStart =  userSpanEnd + msgParts[1].length + 1
    val spans = mapOf (
        data.user_color!! to Pair(userSpanStart, userSpanEnd),
        data.box_color!! to Pair(boxSpanStart, boxSpanStart + 1 + data.box_name!!.length)
    )
    fillSpannable(spannable, spans)
    view.text = spannable
}

internal fun fillSpannable(spannable: Spannable, spans: Map<String, Pair<Int, Int>>) {
    spans.forEach { (color, bounds) ->
        val (start, end) = bounds
        spannable.setSpan(
                ForegroundColorSpan(Color.parseColor(color)),
                start, end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
                StyleSpan(NORMAL),
                start, end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
}
