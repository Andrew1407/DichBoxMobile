package com.diches.dichboxmobile.view.boxesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R

class AddBox: Fragment() {

    interface AddBoxRedirect {
        fun changeFragmentToBoxInfo()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_box, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)

        val redirector = requireActivity() as AddBoxRedirect
        cancelBtn.setOnClickListener {
            parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.boxesContainer, BoxesInfo(), "BOXES_LIST_TAG")
                    .commit()
            redirector.changeFragmentToBoxInfo()
        }
    }
}