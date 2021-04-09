package com.diches.dichboxmobile.tools

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun decorateView(
        element: TextView,
        prefix: String,
        parameters: Pair<String, Int>
) {
    val (value, color) = parameters
    val text = prefix + value
    val spannable = SpannableString(text)
    spannable.setSpan(
            ForegroundColorSpan(color),
            prefix.length, text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    element.text = spannable
}

fun fillView(element: TextView, parameters: Pair<String, String>) {
    val (text, color) = parameters
    element.text = text
    element.setTextColor(Color.parseColor(color))
}
