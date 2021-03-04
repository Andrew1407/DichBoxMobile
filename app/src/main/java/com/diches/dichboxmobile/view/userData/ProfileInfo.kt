package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.userDataManager.UserProfiler
import kotlinx.coroutines.runBlocking

class ProfileInfo: Fragment() {
    private val userProfiler = UserProfiler()
    private lateinit var userData: UserContainer.UserData

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userData = userProfiler.getUserData(requireContext(), savedInstanceState)
        handleInfoFields(view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        userProfiler.saveUserDataState(outState)
    }


    private fun handleInfoFields(view: View) {
        val username = view.findViewById<TextView>(R.id.userName)
        val description = view.findViewById<TextView>(R.id.userDescription)
        val email = view.findViewById<TextView>(R.id.userEmailText)
        val signedDate = view.findViewById<TextView>(R.id.userSignedDate)
        val followers = view.findViewById<TextView>(R.id.userFollowers)
        val logo =  view.findViewById<ImageView>(R.id.userLogo)

        userProfiler
                .fillUsername(username)
                .fillDescription(description)
                .fillEmail(email)
                .fillDate(signedDate)
                .fillFollowers(followers)
                .fillLogo(logo)
    }
}