package com.diches.dichboxmobile.view.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.UserStateViewModel

class VisitedUser : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =  inflater.inflate(R.layout.fragment_user_visited, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameView = view.findViewById<TextView>(R.id.userName)
        val userStateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val userName = userStateViewModel.namesState.value!!.second
        nameView.text = userName
        //println(userStateViewModel.namesState.value)
    }
}