package com.diches.dichboxmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.mv.userDataManager.UserStateViewModel
import com.diches.dichboxmobile.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), Search.Redirector {
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navFragments: List<Fragment>
    private lateinit var activeFragment: Fragment
    private lateinit var tagList: List<String>
    private lateinit var homePageIcon: ImageView
    private lateinit var viewModel: UserStateViewModel
    private var currentNavPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(UserStateViewModel::class.java)

        if (savedInstanceState == null) initUserPresence()
        setContentView(R.layout.activity_main)
        setUpTitle()
        handleNavBar(savedInstanceState)

        initListeners()
        goToHomePage()
        setUserBtnColor()
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

    private fun goToHomePage() {
        homePageIcon = findViewById(R.id.homePageIcon)
        homePageIcon.setOnClickListener {
            val oldNamesState = viewModel.namesState.value!!
            viewModel.setState(oldNamesState.copy(second = oldNamesState.first))
            redirectToUserPage()
        }
    }

    private fun setUserBtnColor() {
        val isSigned = applicationContext.getFileStreamPath("signed.txt")!!.exists()
        if (isSigned) homePageIcon.setBackgroundResource(R.drawable.central_item_signed)
        viewModel.namesState.observe(this) { (signedName, _) ->
            val background = if (signedName != null) R.drawable.central_item_signed
                else R.drawable.central_item_unsigned
            homePageIcon.setBackgroundResource(background)
        }
    }

    override fun handleRedirection() {
        redirectToUserPage()
    }

    private fun redirectToUserPage() {
        if (currentNavPosition == 0) return
        currentNavPosition = 0
        supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(navFragments[currentNavPosition])
                .commit()
        bottomNav.selectedItemId = R.id.userOption
    }

    private fun initUserPresence() {
        val isSigned = getFileStreamPath("signed.txt")!!.exists()
        if (!isSigned) {
            viewModel.setState(Pair(null, null))
            return
        }
        openFileInput("signed.txt").use { stream ->
            val name = stream?.bufferedReader().use { it?.readText() }
            viewModel.setState(Pair(name, name))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", currentNavPosition)
    }
}