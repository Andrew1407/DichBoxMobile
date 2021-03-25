package com.diches.dichboxmobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.settings.RemoveAccountOption
import com.diches.dichboxmobile.mv.settings.SignOutOption
import com.diches.dichboxmobile.mv.verifiers.signVerifiers.SignViewModel
import org.w3c.dom.Text

class Settings : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(SignViewModel::class.java)

        val signOutBtn = view.findViewById<TextView>(R.id.signOut)
        val rmAccountBtn = view.findViewById<TextView>(R.id.removeUserAccount)

        val signOutOption = SignOutOption(requireContext(), viewModel)
        val rmAccOption = RemoveAccountOption(requireContext(), viewModel)
        signOutOption.handleOptionAction(signOutBtn, "Sign out")
        rmAccOption.handleOptionAction(rmAccountBtn, "Remove this account")

        val settingsArea = view.findViewById<LinearLayout>(R.id.settingsOptions)
        val notSignedText = view.findViewById<TextView>(R.id.settingsNotSigned)
        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        settingsArea.visibility = if (isSigned) View.VISIBLE else View.GONE
        notSignedText.visibility = if (isSigned) View.GONE else View.VISIBLE

        viewModel.isSigned.observe(viewLifecycleOwner) { signed ->
            settingsArea.visibility = if (signed) View.VISIBLE else View.GONE
            notSignedText.visibility = if (signed) View.GONE else View.VISIBLE
        }

    }
}