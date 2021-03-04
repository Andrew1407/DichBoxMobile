package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.skydoves.colorpickerview.ColorPickerDialog

class ProfileEditor: Fragment() {
    private lateinit var changeNameColorBtn: Button
    private lateinit var changeDescColorBtn: Button
    private lateinit var changeLogoBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_editor, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeNameColorBtn = view.findViewById(R.id.editUsernameColor)
        changeDescColorBtn = view.findViewById(R.id.editDescriptionColor)
        changeLogoBtn = view.findViewById(R.id.changeLogoBtn)

        setColorPikerDialog(changeNameColorBtn, "Username color")
        setColorPikerDialog(changeDescColorBtn, "Description color")
    }

    private fun setColorPikerDialog(btn: Button, title: String) {
        btn.setOnClickListener {
            ColorPickerDialog.Builder(context, R.style.colorPickerTheme)
                    .setTitle(title)
                    .setPositiveButton("ok") { _, _ -> 0}
                    .setNegativeButton("cancel") { _, _ -> 0}
                    .attachAlphaSlideBar(false) // the default value is true.
                    .attachBrightnessSlideBar(true) // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show()
        }
    }
}