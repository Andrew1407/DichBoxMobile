package com.diches.dichboxmobile.view.boxesList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class BoxesList : Fragment() {
    private val tags = listOf("UNAUTHORISED_TAG", "BOXES_LIST_TAG", "BOXES_ADD_TAG", "BOXES_ENTRIES_TAG")
    private var currentPosition: Int = 0
    private lateinit var stateViewModel: UserStateViewModel

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_boxes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val currentFragment = getCurrentFragment(savedInstanceState)
        val curTag = tags[currentPosition]
        setFragmentVisible(currentFragment, curTag)
        handleStateObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("isSigned", currentPosition)
    }

    private fun handleStateObservers() {
        val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
        stateViewModel.namesState.observe(viewLifecycleOwner) { (signedName, visitedName) ->
            val ownPage = signedName != null && signedName == visitedName
            val atBoxPage = currentPosition == 2
            val boxOpened = boxViewModel.boxName.value != null
            if (ownPage && atBoxPage || boxOpened) return@observe
            val boxesShown = visitedName != null
            currentPosition = if (boxesShown) 1 else 0
            val tag = tags[currentPosition]
            val initialFragment = childFragmentManager.findFragmentByTag(tag)
            val fragment = initialFragment ?: if (boxesShown) BoxesInfo() else BoxesNone()
            setFragmentVisible(fragment, tag)
        }
        boxViewModel.boxName.observe(viewLifecycleOwner) {
            if (it != null) {
                currentPosition = 3
                return@observe
            }
            val boxesShown = stateViewModel.namesState.value!!.second != null
            currentPosition = if (boxesShown) 1 else 0
            val tag = tags[currentPosition]
            val initialFragment = childFragmentManager.findFragmentByTag(tag)
            val fragment = initialFragment ?: if (boxesShown) BoxesInfo() else BoxesNone()
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
        val boxesShown = stateViewModel.namesState.value!!.second != null
        currentPosition = if (boxesShown) 1 else 0
        if (boxesShown) BoxesInfo() else BoxesNone()
    }

    fun setCurrentPosition(position: Int) {
        currentPosition = position
    }

    fun redirectToBoxesList() {
        setCurrentPosition(1)
        val tag = tags[currentPosition]
        val initialFragment = childFragmentManager.findFragmentByTag(tag)
        val fragment = initialFragment ?: BoxesInfo()
        setFragmentVisible(fragment, tag)
    }
}