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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.diches.dichboxmobile.view.Boxes
import com.diches.dichboxmobile.view.Search
import com.diches.dichboxmobile.view.Settings
import com.diches.dichboxmobile.view.User
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    private fun clckTest() {
        userAPI = UserAPI()
        findViewById<ImageView>(R.id.homePageIcon).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                println(userAPI.search(UserContainer.SearchedChunk("o")))
            }
        }
    }
}