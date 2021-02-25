package com.diches.dichboxmobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.MainActivity
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.view.signForms.FragmentCleaner
import com.diches.dichboxmobile.view.signForms.SignIn
import com.diches.dichboxmobile.view.signForms.SignUp
import com.diches.dichboxmobile.view.signForms.ViewPagerAdapter
import java.io.File

class User : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isSigned = context?.getFileStreamPath("signed.txt")!!.exists()
        val containers = listOf(Profile(), SignArea())
        val curFragment = if (isSigned) 0 else 1
        containers.forEach {
            childFragmentManager.beginTransaction().add(R.id.user_container, it).commit()
        }

        childFragmentManager.beginTransaction().replace(R.id.user_container, containers[curFragment]).commit()
    }


}