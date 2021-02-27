package com.diches.dichboxmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navFragments: List<Fragment>
    private lateinit var activeFragment: Fragment
    private lateinit var tagList: List<String>

    private lateinit var userAPI: UserAPI
    private var currentNavPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setUpTitle()
        handleNavBar(savedInstanceState)

        initListeners()
        clckTest()
    }

    private fun handleNavBar(savedInstanceState: Bundle?) {
        val isInstSaved = savedInstanceState != null
        tagList = listOf("USER_TAG", "BOXES_TAG", "SEARCH_TAG", "SETTINGS_TAG")
        navFragments = tagList.map { getFragment(it, isInstSaved) }

        if (isInstSaved) {
            currentNavPosition = savedInstanceState!!.getInt("position")
            initialiseFragments()
        } else {
            initialiseFragments()
            setUpNavBar()
        }
    }

    private fun getFragment(tag: String, isSaved: Boolean): Fragment {
        val getExisted = { supportFragmentManager.findFragmentByTag(tag) }

        val res = when(tag) {
            "USER_TAG" -> if (isSaved) getExisted() else User()
            "BOXES_TAG" -> if (isSaved) getExisted() else Boxes()
            "SEARCH_TAG" -> if (isSaved) getExisted() else Search()
            else -> if (isSaved) getExisted() else Settings()
        }

        return res as Fragment
    }

    private fun setUpTitle() {
        toolbar = findViewById(R.id.mainTitleBar)
        setSupportActionBar(toolbar)
        title = "DichBox"
        toolbar.setTitleTextAppearance(this, R.style.DynarShadowCFont)
    }

    private fun initialiseFragments() {
        bottomNav = findViewById(R.id.bottom_nav_view)
        activeFragment = navFragments[currentNavPosition]
    }

    private fun setUpNavBar() {
        supportFragmentManager.beginTransaction().apply {
            for ((i, fragment) in navFragments.withIndex()) {
                val transaction: FragmentTransaction = add(R.id.container, fragment, tagList[i])
                if (fragment != activeFragment) transaction.hide(fragment)
            }
        }.commit()
    }

    private fun handleListener(position: Int): Boolean {
        if (position == -1) return false
        currentNavPosition = position
        val curFragment = navFragments[position]
        supportFragmentManager
                .beginTransaction()
                .hide(activeFragment)
                .show(curFragment)
                .commit()
        activeFragment = curFragment
        return true
    }

    private fun initListeners() {
        val optionsIds = listOf(
                R.id.userOption, R.id.boxesListOption, R.id.searchOption, R.id.settingsOption
        )
        bottomNav.setOnNavigationItemSelectedListener {
           val position = optionsIds.indexOf(it.itemId)
           handleListener(position)
        }
    }

    private fun clckTest() {
        userAPI = UserAPI()
        findViewById<ImageView>(R.id.homePageIcon).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                println(userAPI.search(UserContainer.SearchedChunk("o")))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", currentNavPosition)
    }
}