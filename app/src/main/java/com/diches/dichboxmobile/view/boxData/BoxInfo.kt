package com.diches.dichboxmobile.view.boxData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.BoxProfiler
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class BoxInfo : Fragment() {
    private lateinit var boxProfiler: BoxProfiler

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
        val boxDataViewModel = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)
        val namesViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)

        val boxName = currentBoxViewModel.boxName.value!!
        val usernames = namesViewModel.namesState.value!!
        val namesArgs = Pair(usernames.first, usernames.second!!)

        boxProfiler = BoxProfiler(boxDataViewModel).fillViewModel(namesArgs, boxName)
        boxDataViewModel.liveData.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            boxProfiler.refreshData()
            handleInfoFields(view)
        }

        val redirector = requireActivity() as BoxInfoRedirect

        otherBoxesBtn.setOnClickListener {
            val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
            boxViewModel.setCurrentBox(null)
            boxDataViewModel.setBoxData(null)
            redirector.changeFragmentToBoxesList()
        }
    }

    private fun handleInfoFields(view: View) {
        val logoView = view.findViewById<ImageView>(R.id.boxLogo)
        val boxNameView = view.findViewById<TextView>(R.id.boxName)
        val boxDescView = view.findViewById<TextView>(R.id.boxDescription)
        val boxCreatorView = view.findViewById<TextView>(R.id.boxCreator)
        val boxTypeView = view.findViewById<TextView>(R.id.boxType)
        val createdView = view.findViewById<TextView>(R.id.boxCreated)
        val editedView = view.findViewById<TextView>(R.id.boxEdited)

        boxProfiler.setLogo(logoView)
                .fillBoxName(boxNameView)
                .fillDescription(boxDescView)
                .fillCreator(boxCreatorView)
                .fillType(boxTypeView)
                .fillDates(createdView, editedView)
    }
}