package com.diches.dichboxmobile.view.signForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.AppColors

class SignArea : Fragment() {
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sign, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentsList: List<Fragment> = listOf(SignUp(), SignIn())
        val signIn: TextView = view.findViewById(R.id.signIn)
        val signUp: TextView = view.findViewById(R.id.signUp)

        viewPager = view.findViewById(R.id.viewPager)
        viewPager.adapter = activity?.let { ViewPagerAdapter(it, fragmentsList) }
        viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                switchSignTitle(position, signUp, signIn)
                fragmentsList.forEach {
                    (it as FragmentCleaner).cleanFieldsInput()
                }
            }
        })
    }

    private fun switchSignTitle(
            position: Int,
            signUp: TextView,
            signIn: TextView
    ) {
        val firstPos: Boolean = position == 0
        signUp.setTextColor(if (firstPos) AppColors.BLACK.raw else AppColors.BLUE.raw)
        signIn.setTextColor(if (firstPos) AppColors.BLUE.raw else AppColors.BLACK.raw)
        signUp.setBackgroundResource(if (firstPos) R.drawable.sign_active else R.drawable.sign_inactive)
        signIn.setBackgroundResource(if (firstPos) R.drawable.sign_inactive else R.drawable.sign_active)
    }
}
