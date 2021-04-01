package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.EditedViewModel
import com.diches.dichboxmobile.mv.userDataManager.UserDataFetcher
import com.diches.dichboxmobile.mv.userDataManager.UserDataViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Profile : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataFetcher = UserDataFetcher()
        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)

        if (userViewModel.liveData.value == null)
            dataFetcher.fillUserViewModel(userViewModel, requireContext())

        val viewPager = view.findViewById<ViewPager2>(R.id.user_viewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val notificationsLabel = getNotificationsLabel(userViewModel.liveData.value!!.notifications!!)
        val options = listOf(
                Pair("profile", ProfileInfo()),
                Pair("edit", ProfileEditor()),
                Pair("subscriptions", Subscriptions()),
                Pair(notificationsLabel, Notifications())
        )

        val vpAdapter = ViewPagerAdapter(this, options.map { it.second })
        viewPager.adapter = vpAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = options.map { it.first }[position]
        }.attach()

        val editedViewModel = ViewModelProvider(requireActivity()).get(EditedViewModel::class.java)
        editedViewModel.isEdited.observe(viewLifecycleOwner) {
            viewPager.currentItem = 0
        }
        setNotificationsListener(userViewModel, tabLayout)
    }

    private fun setNotificationsListener(userViewModel: UserDataViewModel, tabLayout: TabLayout) {
        userViewModel.liveData.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            val notifications = it.notifications!!
            val oldLabel = tabLayout.getTabAt(3)!!.text.toString()
            if (notifications == 0 && oldLabel == "notifications")
                return@observe

            val newLabel = getNotificationsLabel(notifications)
            if (newLabel != oldLabel)
                tabLayout.getTabAt(3)!!.text = getNotificationsLabel(notifications)
        }
    }

    private fun getNotificationsLabel(notifications: Int): String {
        if (notifications == 0) return "notifications"
        val notificationsStr = notifications.toString()
        val notificationsShorten = if (notificationsStr.length < 4) notificationsStr
            else "${notificationsStr.substring(0, 4)}..."
        return "notifications ($notificationsShorten)"
    }
}