package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R

class UserProfile: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        if (isSigned)
            context?.openFileInput("signed.txt").use { stream ->
                val text = stream?.bufferedReader().use {
                    it?.readText()
                }

                view.findViewById<TextView>(R.id.userProfileText).text = text
            }
    }
}