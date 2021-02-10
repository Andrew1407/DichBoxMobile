package com.diches.dichboxmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.mainTitleBar)
        setUpTitle()
    }

    private fun setUpTitle() {
        setSupportActionBar(toolbar)
        title = "DichBox"
        toolbar.setTitleTextAppearance(this, R.style.DynarShadowCFont)
    }
}