package com.diches.dichboxmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.diches.dichboxmobile.view.signForms.SignIn
import com.diches.dichboxmobile.view.signForms.SignUp
import com.diches.dichboxmobile.view.signForms.ViewPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navFragments: List<Fragment>
    private lateinit var activeFragment: Fragment
    private lateinit var userAPI: UserAPI
    private var currentNavPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState != null)
            currentNavPosition = savedInstanceState.getInt("position")

        setUpTitle()
        initialiseFragments()
        setUpNavBar()
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
        navFragments = listOf(User(), Boxes(), Search(), Settings())
        activeFragment = navFragments[currentNavPosition]
    }

    private fun setUpNavBar() {
        supportFragmentManager.beginTransaction().apply {
            navFragments.forEach {
                val transaction: FragmentTransaction = add(R.id.container, it)
                if (it != activeFragment) transaction.hide(it)
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