package com.diches.dichboxmobile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.view.boxes.BoxesInfo
import com.diches.dichboxmobile.view.boxes.BoxesUnauthorised

class Boxes : Fragment() {
    private val tags = listOf("UNAUTHORISED_TAG", "BOXES_TAG")
    private var currentPosition: Int = 0

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_boxes, container, false)

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
        val viewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)

        viewModel.namesState.observe(viewLifecycleOwner) { (signedName, _) ->
            val signed = signedName != null
            currentPosition = if (signed) 1 else 0
            val tag = tags[currentPosition]
            val fragment = if (signed)
                childFragmentManager.findFragmentByTag(tags[1]) ?: BoxesInfo()
            else
                childFragmentManager.findFragmentByTag(tags[0]) ?: BoxesUnauthorised()
            setFragmentVisible(fragment, tag)
        }
    }

    private fun setFragmentVisible(fragment: Fragment, tag: String) {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.boxesContainer, fragment, tag)
                .commit()
    }

    private fun getCurrentFragment(bundle: Bundle?): Fragment = if (bundle != null) {
        currentPosition = bundle.getInt("isSigned")
        val tag = tags[currentPosition]
        childFragmentManager.findFragmentByTag(tag) as Fragment
    } else {
        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        currentPosition = if (isSigned) 1 else 0
        if (isSigned) BoxesInfo() else BoxesUnauthorised()
    }
}