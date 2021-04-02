package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.UserDataFetcher
import com.diches.dichboxmobile.mv.userDataManager.profilers.VisitedProfiler
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class VisitedUser : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =  inflater.inflate(R.layout.fragment_user_visited, container, false)
    private lateinit var userProfiler: VisitedProfiler
    private lateinit var followers: TextView
    private lateinit var username: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.userName)
        followers = view.findViewById(R.id.userFollowers)
        val subAction = view.findViewById<TextView>(R.id.userSubscription)
        val userStateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val userDataViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val dataFetcher = UserDataFetcher()
        if (savedInstanceState == null)
            dataFetcher.fillUserViewModel(userDataViewModel, userStateViewModel)

        userProfiler = VisitedProfiler(userStateViewModel, subAction)
                .handleSubscriptionView(followers, userDataViewModel)
        userProfiler.setUserData(userDataViewModel, savedInstanceState)

        handleInfoFields(view)

        userStateViewModel.namesState.observe(viewLifecycleOwner) { (_, visitedName) ->
            val name = username.text.toString()
            if (visitedName == null || name == visitedName) return@observe
            dataFetcher.fillUserViewModel(userDataViewModel, userStateViewModel)
            userProfiler.refreshData(userDataViewModel.liveData.value!!)
            handleInfoFields(view)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        userProfiler.saveUserDataState(outState)
    }

    private fun handleInfoFields(view: View) {
        val description = view.findViewById<TextView>(R.id.userDescription)
        val signedDate = view.findViewById<TextView>(R.id.userSignedDate)
        val logo = view.findViewById<ImageView>(R.id.userLogo)

        userProfiler
                .fillUsername(username)
                .fillDescription(description)
                .fillDate(signedDate)
                .fillFollowers(followers)
                .fillLogo(logo)
    }
}