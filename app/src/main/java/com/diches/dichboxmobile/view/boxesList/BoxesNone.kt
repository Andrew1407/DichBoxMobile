package com.diches.dichboxmobile.view.boxesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R

class BoxesNone : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.boxes_none, container, false)
}
