package com.diches.dichboxmobile.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.view.signForms.SignIn
import com.diches.dichboxmobile.view.signForms.SignUp
import com.diches.dichboxmobile.view.signForms.ViewPagerAdapter

class User : Fragment() {
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user, container, false)

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
            }
        })
    }

    fun switchSignTitle(
        position: Int,
        signUp: TextView,
        signIn: TextView
    ) {
        val blue: Int = -0xff2601
        val black: Int = -0x1000000
        val firstPos: Boolean = position == 0

        signUp.setTextColor(if (firstPos) black else blue)
        signIn.setTextColor(if (firstPos) blue else black)
        signUp.setBackgroundResource(if (firstPos) R.drawable.sign_active else R.drawable.sign_inactive)
        signIn.setBackgroundResource(if (firstPos) R.drawable.sign_inactive else R.drawable.sign_active)
    }
}