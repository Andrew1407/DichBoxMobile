package com.diches.dichboxmobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.signVerifiers.SignViewModel
import com.diches.dichboxmobile.view.signForms.SignArea
import com.diches.dichboxmobile.view.userData.Profile

class User : Fragment() {
    private lateinit var userProfile: Profile
    private lateinit var signArea: SignArea

      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
          retainInstance = true
          return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        val tags = listOf("PROFILE_TAG", "SIGN_AREA_TAG")
        val signFragment = childFragmentManager.findFragmentByTag(tags[1])
        val profileFragment = childFragmentManager.findFragmentByTag(tags[0])
        val curFragment = if (isSigned) 0 else 1

        if (savedInstanceState != null) {
            userProfile = if (profileFragment != null) profileFragment as Profile else Profile()
            signArea = if (signFragment != null) signFragment as SignArea else SignArea()
        } else {
            userProfile = Profile()
            signArea = SignArea()
        }

        val containers = listOf(userProfile, signArea)
        childFragmentManager
                .beginTransaction()
                .replace(R.id.user_container, containers[curFragment], tags[curFragment]).commit()

        val viewModel = ViewModelProvider(requireActivity()).get(SignViewModel::class.java)
        viewModel.isSigned.observe(viewLifecycleOwner, {
            val fragment = if (it) Profile() else SignArea()
            val tagInd = if (it) 0 else 1
            childFragmentManager
                    .beginTransaction()
                    .replace(R.id.user_container, fragment, tags[tagInd]).commit()
        })
    }


}