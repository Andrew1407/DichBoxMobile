package com.diches.dichboxmobile.view.boxData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxEditedViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.view.userData.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BoxEntries : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val stateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)

        val viewPager = view.findViewById<ViewPager2>(R.id.boxViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.boxTabLayout)

        val signedName = stateViewModel.namesState.value!!.first
        val ownPage = if (signedName == null ) false else userViewModel.liveData.value!!.ownPage

        val allOptions = listOf(
            Pair("info", BoxInfo()),
            Pair("edit", BoxEditor()),
            Pair("files", BoxFiles()),
            Pair("opened files", OpenedFiles())
        )

        val tabOptions = if (ownPage) allOptions else allOptions.minus(allOptions[1])

        val vpAdapter = ViewPagerAdapter(this, tabOptions.map { it.second })
        viewPager.adapter = vpAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabOptions.map { it.first }[position]
        }.attach()

        val editedViewModel = ViewModelProvider(requireActivity()).get(BoxEditedViewModel::class.java)
        editedViewModel.isEdited.observe(viewLifecycleOwner) {
            if (!it) return@observe
            viewPager.currentItem = 0
            editedViewModel.setEdited(false)
        }
    }
}