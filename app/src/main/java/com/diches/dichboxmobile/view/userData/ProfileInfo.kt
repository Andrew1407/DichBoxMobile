package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.profilers.UserProfiler
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class ProfileInfo: Fragment() {
    private lateinit var userProfiler: UserProfiler

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDataVM = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val usernamesVM = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)

        userProfiler = UserProfiler(usernamesVM, userDataVM).setUserData()
        userDataVM.liveData.observe(viewLifecycleOwner) {
            if (it == null || !it.ownPage) return@observe
            userProfiler.refreshData(it)
            handleInfoFields(view)
        }

        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refreshUserInfo)
        refreshLayout.setOnRefreshListener {
            userProfiler.refetchData(this.requireActivity(), refreshLayout)
            handleInfoFields(view)
        }
    }

    private fun handleInfoFields(view: View) {
        val username = view.findViewById<TextView>(R.id.userName)
        val description = view.findViewById<TextView>(R.id.userDescription)
        val email = view.findViewById<TextView>(R.id.userEmailText)
        val signedDate = view.findViewById<TextView>(R.id.userSignedDate)
        val followers = view.findViewById<TextView>(R.id.userFollowers)
        val logo = view.findViewById<ImageView>(R.id.userLogo)

        userProfiler
                .fillUsername(username)
                .fillDescription(description)
                .fillEmail(email)
                .fillDate(signedDate)
                .fillFollowers(followers)
                .fillLogo(logo)
    }
}