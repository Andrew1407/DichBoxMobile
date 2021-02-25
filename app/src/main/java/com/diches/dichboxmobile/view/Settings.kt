package com.diches.dichboxmobile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diches.dichboxmobile.R

class Settings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = view.findViewById<TextView>(R.id.settingsText)
        text.setOnClickListener {
            val signedFile = context?.getFileStreamPath("signed.txt")
            if (signedFile!!.exists()) signedFile.delete()
        }
    }
}