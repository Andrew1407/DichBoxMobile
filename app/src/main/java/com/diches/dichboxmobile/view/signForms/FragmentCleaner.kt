package com.diches.dichboxmobile.view.signForms

import android.widget.EditText
import androidx.core.graphics.drawable.DrawableCompat
import com.diches.dichboxmobile.tools.AppColors

interface FragmentCleaner {
    fun cleanFieldsInput()
    fun cleanInput(input: EditText) {
        val blue = AppColors.BLUE.raw
        if (input.text.isNotEmpty()) input.text.clear()
        DrawableCompat.setTint(input.background, blue)
    }
}