package com.diches.dichboxmobile.view.boxesList

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
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxesListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.view.boxData.AddBox
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BoxesInfo : Fragment() {
    private lateinit var listHandler: BoxesListPresenter
    private lateinit var nameArgs: Pair<String?, String>

    interface BoxesInfoRedirect {
        fun changeFragmentToBoxAdd()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_boxes_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val stateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val boxesListViewModel = ViewModelProvider(requireActivity()).get(BoxesListViewModel::class.java)
        val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
        val namesState = stateViewModel.namesState.value!!

        val listView = view.findViewById<ListView>(R.id.boxesList)
        val boxesSearch = view.findViewById<EditText>(R.id.boxesSearch)
        val typesSpinner = view.findViewById<Spinner>(R.id.boxesSearchSpinner)
        val addBoxBtn = view.findViewById<FloatingActionButton>(R.id.addBoxIcon)

        val userData = userViewModel.liveData.value
        val ownPage = when {
            (namesState.first == null) -> false
            (userData == null) -> true
            else -> userData.ownPage
        }

        setAddBoxBtnVisibility(addBoxBtn, ownPage)
        nameArgs = Pair(namesState.first, namesState.second!!)
        listView.emptyView = view.findViewById(R.id.boxesListEmpty)

        listHandler = BoxesListPresenter(this, listView, typesSpinner, boxesListViewModel)
            .createListAdapter(savedInstanceState, nameArgs, boxViewModel)
            .handleSearch(boxesSearch)
            .createSpinnerAdapter(savedInstanceState, ownPage)

        val redirector = requireActivity() as BoxesInfoRedirect

        addBoxBtn.setOnClickListener {
            parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.boxesContainer, AddBox(), "BOXES_ADD_TAG")
                    .commit()
            redirector.changeFragmentToBoxAdd()
        }

        userViewModel.liveData.observe(viewLifecycleOwner) {
            if (it == null || it.name == nameArgs.second) return@observe
            val newName = it.name
            val namesRefreshed = nameArgs.copy(second = newName)
            val isOwnPage = userViewModel.liveData.value!!.ownPage
            setAddBoxBtnVisibility(addBoxBtn, isOwnPage)
            listHandler
                .setBoxesList(null, namesRefreshed)
                .refreshData(isOwnPage)
            nameArgs = namesRefreshed
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        listHandler.saveBoxesState(outState)
    }

    private fun setAddBoxBtnVisibility(btn: FloatingActionButton, ownPage: Boolean) {
        btn.visibility = if (ownPage) View.VISIBLE else View.GONE
    }
}