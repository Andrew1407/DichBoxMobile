package com.diches.dichboxmobile.mv.boxesDataManager

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxesListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.view.boxData.BoxEntries
import kotlinx.coroutines.runBlocking

class BoxesListPresenter(
    private val fragment: Fragment,
    private val listView: ListView,
    private val typesSpinner: Spinner,
    private val boxesListViewModel: BoxesListViewModel
) {
    private val api = BoxesAPI()
    lateinit var boxes: List<BoxesContainer.BoxDataListItem>
    lateinit var boxesShown: List<BoxesContainer.BoxDataListItem>
    lateinit var inputSearch: EditText

    fun setBoxesList(bundle: Bundle?, names: Pair<String?, String>): BoxesListPresenter {
        boxes = if (bundle == null) getBoxesByRequest(names)
            else boxesListViewModel.liveData.value!!.first
        return this
    }

    private fun getBoxesByRequest(names: Pair<String?, String>): List<BoxesContainer.BoxDataListItem> {
        val (viewerName, boxOwnerName) = names
        val getBody = BoxesContainer.UserBoxesReq(viewerName, boxOwnerName)
        val (st, res) = runBlocking { api.getUserBoxes(getBody) }
        if (Statuses.OK.eqNot(st)) return emptyList()
        val (boxesList) = (res as BoxesContainer.UserBoxes)
        return boxesList
    }

    fun createListAdapter(
            bundle: Bundle?,
            names: Pair<String?, String>,
            boxState: CurrentBoxViewModel,
            userState: UserStateViewModel
    ): BoxesListPresenter {
        boxes = if (bundle == null) getBoxesByRequest(names)
            else boxesListViewModel.liveData.value!!.first
        boxesShown = if (bundle == null) boxes.toList() else
            boxesListViewModel.liveData.value!!.second
        listView.adapter = BoxesListAdapter(
                fragment.requireContext(), R.layout.boxes_list_item,
                boxes.toList(), boxesShown.toList()
        ) { boxName, ownerName ->
            val oldNames = userState.namesState.value!!
            if (oldNames.first != ownerName)
                userState.setState(oldNames.copy(second = ownerName))
            boxState.setCurrentBox(boxName)
            fragment.parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.boxesContainer, BoxEntries(), "BOXES_ENTRIES_TAG")
                    .commit()
        }
        return this
    }

    fun handleSearch(input: EditText): BoxesListPresenter {
        inputSearch = input
        inputSearch.addTextChangedListener {
            val currentType = typesSpinner.selectedItem.toString()
            filterBoxesByType(currentType)
        }
        return this
    }

    private fun filterBoxesByType(type: String) {
        val listViewFilter = (listView.adapter as Filterable).filter
        val filterParam = "${inputSearch.text} $type"
        listViewFilter.filter(filterParam)
    }

    fun createSpinnerAdapter(bundle: Bundle?, ownPage: Boolean): BoxesListPresenter {
        val boxesTypes = getBoxesType(ownPage)
        val adapter = TypesSpinnerAdapter(fragment.requireContext(), boxesTypes)
        typesSpinner.adapter = adapter
        typesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val currentType = parent?.getItemAtPosition(position).toString()
                filterBoxesByType(currentType)
            }
        }

        if (bundle != null) {
            val savedPosition = bundle.getInt("spinnerPosition")
            typesSpinner.setSelection(savedPosition)
        }

        return this
    }

    fun saveBoxesState(bundle: Bundle) {
        val spinnerPosition = typesSpinner.selectedItemPosition
        bundle.putInt("spinnerPosition", spinnerPosition)
        val boxesState = Pair(boxes, (listView.adapter as BoxesListAdapter).itemsShown)
        boxesListViewModel.setBoxesList(boxesState)
    }

    fun refreshData(ownPage: Boolean): BoxesListPresenter {
        val listAdapter = listView.adapter as BoxesListAdapter
        val spinnerAdapter = typesSpinner.adapter as TypesSpinnerAdapter
        listAdapter.items = boxes
        listAdapter.itemsShown = boxes
        inputSearch.text.clear()
        typesSpinner.setSelection(0)
        spinnerAdapter.items = getBoxesType(ownPage)
        spinnerAdapter.notifyDataSetChanged()
        listAdapter.notifyDataSetChanged()

        return this
    }

    private fun getBoxesType(ownPage: Boolean): List<String> = if (ownPage) listOf(
        "all", "public", "private", "followers", "limited" , "invetee"
    ) else listOf(
        "all", "public", "followers", "limited"
    )
}
