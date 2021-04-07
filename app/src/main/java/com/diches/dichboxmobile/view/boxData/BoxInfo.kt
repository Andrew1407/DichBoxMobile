package com.diches.dichboxmobile.view.boxData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.CurrentBoxViewModel

class BoxInfo : Fragment() {
    interface BoxInfoRedirect {
        fun changeFragmentToBoxesList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val otherBoxesBtn = view.findViewById<Button>(R.id.otherBoxesBtn)
        val currentBoxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)

        println(currentBoxViewModel.boxName.value)

        val redirector = requireActivity() as BoxInfoRedirect

        otherBoxesBtn.setOnClickListener {
            val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
            boxViewModel.setCurrentBox(null)
            redirector.changeFragmentToBoxesList()
        }
    }
}