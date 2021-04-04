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
import com.diches.dichboxmobile.mv.boxesDataManager.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.settings.RemoveAccountOption
import com.diches.dichboxmobile.mv.settings.SignOutOption
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class Settings : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userStateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val boxStateViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
        val boxViewModel = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)

        val userState = Pair(userStateViewModel, userViewModel)
        val boxState = Pair(boxStateViewModel, boxViewModel)

        val signOutBtn = view.findViewById<TextView>(R.id.signOut)
        val rmAccountBtn = view.findViewById<TextView>(R.id.removeUserAccount)

        val signOutOption = SignOutOption(requireContext(), userState, boxState)
        val rmAccOption = RemoveAccountOption(requireContext(), userState, boxState)
        signOutOption.handleOptionAction(signOutBtn, "Sign out")
        rmAccOption.handleOptionAction(rmAccountBtn, "Remove this account")

        val settingsArea = view.findViewById<LinearLayout>(R.id.settingsOptions)
        val notSignedText = view.findViewById<TextView>(R.id.settingsNotSigned)
        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        settingsArea.visibility = if (isSigned) View.VISIBLE else View.GONE
        notSignedText.visibility = if (isSigned) View.GONE else View.VISIBLE

        userStateViewModel.namesState.observe(viewLifecycleOwner) { (signedName, _) ->
            settingsArea.visibility = if (signedName != null) View.VISIBLE else View.GONE
            notSignedText.visibility = if (signedName != null) View.GONE else View.VISIBLE
        }

    }
}