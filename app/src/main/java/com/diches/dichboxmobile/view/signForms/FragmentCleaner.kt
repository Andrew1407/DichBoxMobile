package com.diches.dichboxmobile.view.signForms

import android.widget.EditText
import androidx.core.graphics.drawable.DrawableCompat

interface FragmentCleaner {
    fun cleanFieldsInput()
    fun cleanInput(input: EditText) {
        val blue = -0xff2601
        if (input.text.isNotEmpty()) input.text.clear()
        DrawableCompat.setTint(input.background, blue)
    }
}