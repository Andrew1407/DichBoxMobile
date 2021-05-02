package com.diches.dichboxmobile.view.boxData.openedFiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel

class OpenedContainer : Fragment() {
    private val tags = listOf("NONE_FILES_TAG", "OPENED_FILES_TAG")
    private var currentPosition: Int = 0
    private lateinit var filesViewModel: OpenedFilesViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box_opened_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filesViewModel = ViewModelProvider(requireActivity()).get(OpenedFilesViewModel::class.java)
        val currentFragment = getCurrentFragment(savedInstanceState)
        val curTag = tags[currentPosition]
        setFragmentVisible(currentFragment, curTag)
        handleStateObserver()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("openedFilesState", currentPosition)
    }

    private fun handleStateObserver() {
        filesViewModel.liveData.observe(viewLifecycleOwner) {
            val isOpened = filesViewModel.liveData.value != null
            if (isOpened && currentPosition == 1) return@observe
            currentPosition = if (isOpened) 1 else 0
            val tag = tags[currentPosition]
            val tempFragment = childFragmentManager.findFragmentByTag(tag)
            val fragment = tempFragment ?: if (isOpened) OpenedFilesList() else OpenedFilesNone()
            setFragmentVisible(fragment, tag)
        }
    }

    private fun setFragmentVisible(fragment: Fragment, tag: String) {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.boxOpenedContainer, fragment, tag)
                .commit()
    }

    private fun getCurrentFragment(bundle: Bundle?): Fragment = if (bundle != null) {
        currentPosition = bundle.getInt("openedFilesState")
        val tag = tags[currentPosition]
        childFragmentManager.findFragmentByTag(tag) as Fragment
    } else {
        val isOpened = filesViewModel.liveData.value != null
        currentPosition = if (isOpened) 1 else 0
        if (isOpened) OpenedFilesList() else OpenedFilesNone()
    }
}
