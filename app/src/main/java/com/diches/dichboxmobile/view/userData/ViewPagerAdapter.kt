package com.diches.dichboxmobile.view.userData

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
        fragment: Fragment,
        private val optionsList: List<Fragment>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = optionsList.size
    override fun createFragment(position: Int): Fragment = optionsList[position]

}