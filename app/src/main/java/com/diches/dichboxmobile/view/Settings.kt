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
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.*
import com.diches.dichboxmobile.mv.settings.RemoveAccountOption
import com.diches.dichboxmobile.mv.settings.SignOutOption
import com.diches.dichboxmobile.mv.userDataManager.notifications.NotificationsViewModel
import com.diches.dichboxmobile.mv.userDataManager.subscriptions.SubscriptionsViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.mv.usersSearch.UsersSearchViewModel

class Settings : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewStates = listOf(
                UserStateViewModel::class.java,
                CurrentBoxViewModel::class.java,
                BoxDataViewModel::class.java,
                FilesListViewModel::class.java,
                OpenedFilesViewModel::class.java,
                UsersSearchViewModel::class.java,
                UserDataViewModel::class.java,
                SubscriptionsViewModel::class.java,
                NotificationsViewModel::class.java,
                FilesListViewModel::class.java,
                EditorsCopyViewModel::class.java,
                ViewersCopyViewModel::class.java,
                BoxesListViewModel::class.java,
                EditorsViewModel::class.java,
                ViewersViewModel::class.java
        ).map { ViewModelProvider(requireActivity()).get(it) }

        val signOutBtn = view.findViewById<TextView>(R.id.signOut)
        val rmAccountBtn = view.findViewById<TextView>(R.id.removeUserAccount)

        val signOutOption = SignOutOption(requireContext(), viewStates)
        val rmAccOption = RemoveAccountOption(requireContext(), viewStates)
        signOutOption.handleOptionAction(signOutBtn, "Sign out")
        rmAccOption.handleOptionAction(rmAccountBtn, "Remove this account")

        val settingsArea = view.findViewById<LinearLayout>(R.id.settingsOptions)
        val notSignedText = view.findViewById<TextView>(R.id.settingsNotSigned)
        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        settingsArea.visibility = if (isSigned) View.VISIBLE else View.GONE
        notSignedText.visibility = if (isSigned) View.GONE else View.VISIBLE

        val userStateVM = viewStates[0] as UserStateViewModel
        userStateVM.namesState.observe(viewLifecycleOwner) { (signedName, _) ->
            settingsArea.visibility = if (signedName != null) View.VISIBLE else View.GONE
            notSignedText.visibility = if (signedName != null) View.GONE else View.VISIBLE
        }

    }
}