package com.diches.dichboxmobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.verifiers.signVerifiers.SignViewModel
import com.diches.dichboxmobile.view.signForms.SignArea
import com.diches.dichboxmobile.view.userData.Profile

class User : Fragment() {
    private val tags = listOf("SIGN_AREA_TAG", "PROFILE_TAG")
    private var currentPosition: Int = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFragment = getCurrentFragment(savedInstanceState)
        val curTag = tags[currentPosition]
        setFragmentVisible(currentFragment, curTag)
        handleSignedStateObserver()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("isSigned", currentPosition)
    }

    private fun handleSignedStateObserver() {
        val viewModel = ViewModelProvider(requireActivity()).get(SignViewModel::class.java)

        viewModel.isSigned.observe(viewLifecycleOwner) { signed ->
            currentPosition = if (signed) 1 else 0
            val tag = tags[currentPosition]
            val fragment = if (signed)
                childFragmentManager.findFragmentByTag(tags[1]) ?: Profile()
            else
                childFragmentManager.findFragmentByTag(tags[0]) ?: SignArea()
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
        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        currentPosition = if (isSigned) 1 else 0
        if (isSigned) Profile() else SignArea()
    }
}