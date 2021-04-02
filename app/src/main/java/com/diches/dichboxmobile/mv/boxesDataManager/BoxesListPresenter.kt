package com.diches.dichboxmobile.mv.boxesDataManager

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.runBlocking

class BoxesListPresenter(
    private val context: Context,
    private val listView: ListView,
    private val typesSpinner: Spinner
) {
    private val api = BoxesAPI()
    lateinit var boxes: List<BoxesContainer.BoxDataListItem>
    lateinit var boxesShown: List<BoxesContainer.BoxDataListItem>
    lateinit var inputSearch: EditText

    fun setBoxesList(bundle: Bundle?, names: Pair<String?, String>): BoxesListPresenter {
        boxes = if (bundle == null) getBoxesByRequest(names) else getSavedList("boxesList", bundle)
        return this
    }

    private fun getBoxesByRequest(names: Pair<String?, String>): List<BoxesContainer.BoxDataListItem> {
        val (viewerName, boxOwnerName) = names
        val getBody = BoxesContainer.UserBoxesReq(viewerName, boxOwnerName)
        val (st, res) = runBlocking { api.getUserBoxes(getBody) }
        val (boxesList) = (res as BoxesContainer.UserBoxes)
        return boxesList
    }

    private fun getSavedList(key: String, bundle: Bundle): List<BoxesContainer.BoxDataListItem> {
        val boxesStringified = bundle.getString(key)!!
        val boxesContainer = BoxesContainer
            .parseJSON(boxesStringified, BoxesContainer.UserBoxes::class.java)
        val (boxesList) = (boxesContainer as BoxesContainer.UserBoxes)
        return boxesList
    }

    fun createListAdapter(bundle: Bundle?, names: Pair<String?, String>): BoxesListPresenter {
        boxes = if (bundle == null) getBoxesByRequest(names) else getSavedList("boxesList", bundle)
        boxesShown = if (bundle == null) boxes.toList() else getSavedList("boxesListShown", bundle)
        listView.adapter = BoxesListAdapter(context, R.layout.boxes_list_item, boxes.toList(), boxesShown.toList())
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
        val adapter = TypesSpinnerAdapter(context, boxesTypes)
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
        val stringified = listOf(boxes, (listView.adapter as BoxesListAdapter).itemsShown)
            .map { BoxesContainer.stringifyJSON(BoxesContainer.UserBoxes(it)) }
        bundle.putString("boxesList", stringified[0])
        bundle.putString("boxesListShown", stringified[1])
        bundle.putInt("spinnerPosition", spinnerPosition)
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
