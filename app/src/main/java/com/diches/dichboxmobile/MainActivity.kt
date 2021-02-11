package com.diches.dichboxmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.diches.dichboxmobile.view.Boxes
import com.diches.dichboxmobile.view.Search
import com.diches.dichboxmobile.view.Settings
import com.diches.dichboxmobile.view.User

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navFragments: List<Fragment>
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpTitle()
        initialiseFragments()
        setUpNavBar()
        initListeners()
    }

    private fun setUpTitle() {
        toolbar = findViewById(R.id.mainTitleBar)
        setSupportActionBar(toolbar)
        title = "DichBox"
        toolbar.setTitleTextAppearance(this, R.style.DynarShadowCFont)
    }

    private fun initialiseFragments() {
        bottomNav = findViewById(R.id.bottom_nav_view)
        navFragments = listOf(User(), Boxes(), Search(), Settings())
        activeFragment = navFragments[0]
    }

    private fun setUpNavBar() {
        supportFragmentManager.beginTransaction().apply {
            navFragments.forEach {
                val transaction: FragmentTransaction = add(R.id.container, it)
                if (it !is User) transaction.hide(it)
            }
        }.commit()
    }

    private fun handleListener(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()
        activeFragment = fragment
        return true
    }

    private fun initListeners() {
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.userOption -> handleListener(navFragments[0])
                R.id.boxesListOption -> handleListener(navFragments[1])
                R.id.searchOption -> handleListener(navFragments[2])
                R.id.settingsOption -> handleListener(navFragments[3])
                else -> false
            }
        }
    }
}