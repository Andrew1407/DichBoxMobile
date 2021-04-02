package com.diches.dichboxmobile.view.boxes.boxesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.BoxesListPresenter
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class BoxesInfo : Fragment() {
    private lateinit var listHandler: BoxesListPresenter
    private lateinit var nameArgs: Pair<String?, String>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_boxes_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val stateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val namesState = stateViewModel.namesState.value!!

        val listView = view.findViewById<ListView>(R.id.boxesList)
        val boxesSearch = view.findViewById<EditText>(R.id.boxesSearch)
        val typesSpinner = view.findViewById<Spinner>(R.id.boxesSearchSpinner)

        val ownPage = userViewModel.liveData.value!!.ownPage
        nameArgs = Pair(namesState.first, namesState.second!!)
        listView.emptyView = view.findViewById(R.id.boxesListEmpty)

        listHandler = BoxesListPresenter(requireContext(), listView, typesSpinner)
            .createListAdapter(savedInstanceState, nameArgs)
            .handleSearch(boxesSearch)
            .createSpinnerAdapter(savedInstanceState, ownPage)

        userViewModel.liveData.observe(viewLifecycleOwner) {
            val newName = it.name
            if (newName == nameArgs.second) return@observe
            val namesRefreshed = nameArgs.copy(second = newName)
            listHandler
                .setBoxesList(null, namesRefreshed)
                .refreshData(userViewModel.liveData.value!!.ownPage)
            nameArgs = nameArgs.copy(second = newName)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        listHandler.saveBoxesState(outState)
    }
}