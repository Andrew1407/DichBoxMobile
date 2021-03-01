package com.diches.dichboxmobile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.verifiers.signVerifiers.SignViewModel

class Settings : Fragment() {
    private lateinit var viewModel: SignViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SignViewModel::class.java)

        val text = view.findViewById<TextView>(R.id.settingsText)
        text.setOnClickListener {
            val signedFile = context?.getFileStreamPath("signed.txt")
            if (signedFile!!.exists()) {
                viewModel.setIsSigned(false)
                signedFile.delete()
            }
        }
    }
}