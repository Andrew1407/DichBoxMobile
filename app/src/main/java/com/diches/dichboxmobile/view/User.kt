package com.diches.dichboxmobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.view.signForms.SignArea
import com.diches.dichboxmobile.view.userData.Profile
import com.diches.dichboxmobile.view.userData.VisitedUser

class User : Fragment() {
    private val tags = listOf("SIGN_AREA_TAG", "PROFILE_TAG", "VISITED_TAG")
    private var currentPosition: Int = 0
    private lateinit var viewModel: UserStateViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val currentFragment = getCurrentFragment(savedInstanceState)
        val curTag = tags[currentPosition]
        setFragmentVisible(currentFragment, curTag)
        handleSignedStateObserver()
        savedInstanceState?.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("isSigned", currentPosition)
    }

    private fun handleSignedStateObserver() {
        viewModel.namesState.observe(viewLifecycleOwner) { (signedName, visitedName) ->
            val isSigned = signedName != null
            currentPosition = when {
                signedName != visitedName -> 2
                isSigned -> 1
                else -> 0
            }
            val tag = tags[currentPosition]
            val tempFragment = childFragmentManager.findFragmentByTag(tag)
            val fragment = tempFragment ?: when {
                signedName != visitedName -> VisitedUser()
                isSigned -> Profile()
                else -> SignArea()
            }
            setFragmentVisible(fragment, tag)
        }
    }

    private fun setFragmentVisible(fragment: Fragment, tag: String) {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.user_container, fragment, tag)
                .commit()
    }

    private fun getCurrentFragment(bundle: Bundle?): Fragment = if (bundle != null) {
        currentPosition = bundle.getInt("isSigned")
        val tag = tags[currentPosition]
        childFragmentManager.findFragmentByTag(tag) as Fragment
    } else {
        val isSigned = viewModel.namesState.value!!.first != null
        currentPosition = if (isSigned) 1 else 0
        if (isSigned) Profile() else SignArea()
    }
}