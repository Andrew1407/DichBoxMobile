package com.diches.dichboxmobile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.view.userData.BoxEditor
import com.diches.dichboxmobile.view.userData.UserProfile
import com.diches.dichboxmobile.view.userData.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Profile : Fragment() {
    private lateinit var vpAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var options: List<Pair<String, Fragment>>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.user_viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        options = listOf(
                Pair("profile", UserProfile()),
                Pair("box editor", BoxEditor())
        )
        vpAdapter = ViewPagerAdapter(this, options.map { it.second })
        viewPager.adapter = vpAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = options.map { it.first }[position]
        }.attach()
    }
}