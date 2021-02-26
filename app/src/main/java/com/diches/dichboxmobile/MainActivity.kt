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
    private lateinit var user: User
    private lateinit var boxes: Boxes
    private lateinit var search: Search
    private lateinit var settings: Settings

    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navFragments: List<Fragment>
    private lateinit var activeFragment: Fragment
    private lateinit var userAPI: UserAPI

    private var currentNavPosition: Int = 0
    private val tagList: List<String> = listOf("USER_TAG", "BOXES_TAG", "SEARCH_TAG", "SETTINGS_TAG")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpTitle()

        if (savedInstanceState != null) {
            currentNavPosition = savedInstanceState.getInt("position")
            user = supportFragmentManager.findFragmentByTag(tagList[0]) as User
            boxes = supportFragmentManager.findFragmentByTag(tagList[1]) as Boxes
            search = supportFragmentManager.findFragmentByTag(tagList[2]) as Search
            settings = supportFragmentManager.findFragmentByTag(tagList[3]) as Settings
            initialiseFragments()
        } else {
            user = User()
            boxes = Boxes()
            search = Search()
            settings = Settings()
            initialiseFragments()
            setUpNavBar()
        }
        initListeners()
        clckTest()
    }

    private fun setUpTitle() {
        toolbar = findViewById(R.id.mainTitleBar)
        setSupportActionBar(toolbar)
        title = "DichBox"
        toolbar.setTitleTextAppearance(this, R.style.DynarShadowCFont)
    }

    private fun initialiseFragments() {
        bottomNav = findViewById(R.id.bottom_nav_view)
        navFragments = listOf(user, boxes, search, settings)
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
                R.id.userOption -> {
                    currentNavPosition = 0
                    handleListener(navFragments[0])
                }
                R.id.boxesListOption -> {
                    currentNavPosition = 1
                    handleListener(navFragments[1])
                }
                R.id.searchOption -> {
                    currentNavPosition = 2
                    handleListener(navFragments[2])
                }
                R.id.settingsOption -> {
                    currentNavPosition = 3
                    handleListener(navFragments[3])
                }
                else -> false
            }
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