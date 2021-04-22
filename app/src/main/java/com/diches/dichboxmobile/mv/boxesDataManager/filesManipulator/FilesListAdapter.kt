package com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs.RemoveFileDialog

class FilesListAdapter(
    context: Context,
    private val resource: Int,
    var items: List<BoxesContainer.TypeDir>,
    private val removeFileDialog: RemoveFileDialog,
    private val editor: Boolean
) : ArrayAdapter<BoxesContainer.TypeDir>(context, resource, items), Filterable {
    var itemsShown = items.toList()
    private lateinit var dirRedirect: (name: String, type: String) -> Unit
    private lateinit var rename: (name: String, type: String) -> Unit
    private lateinit var remove: (name: String, type: String) -> Unit

    fun handleRedirection(clb: (name: String, type: String) -> Unit): FilesListAdapter {
        dirRedirect = clb
        return this
    }

    fun handleRemove(clb: (name: String, type: String) -> Unit): FilesListAdapter {
        remove = clb
        return this
    }

    fun handleRename(clb: (name: String, type: String) -> Unit): FilesListAdapter {
        rename = clb
        return this
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater
            .from(context)
            .inflate(resource, parent, false)

        val file = itemsShown[position]
        val fileName = view.findViewById<TextView>(R.id.boxFileName)
        val renameIcon = view.findViewById<ImageView>(R.id.entryRename)
        val removeIcon = view.findViewById<ImageView>(R.id.entryRemove)
        val typeIcon = view.findViewById<ImageView>(R.id.boxFileTypeIcon)

        fileName.text = shortenName(file.name)
        renameIcon.isVisible = editor
        removeIcon.isVisible = editor

        fileName.setOnClickListener { dirRedirect(file.name, file.type) }
        typeIcon.setOnClickListener { dirRedirect(file.name, file.type) }

        if (editor) {
            removeFileDialog.handleOptionAction(removeIcon) {
                remove(file.name, file.type)
            }
            renameIcon.setOnClickListener { rename(file.name, file.type) }
        }

        val typeLogo = if (file.type == "dir") R.drawable.folder_icon else R.drawable.file_icon
        typeIcon.setImageResource(typeLogo)

        return view
    }

    private fun shortenName(str: String): String = if (str.length < 17) str else "${str.slice(0..17)}..."

    override fun getCount(): Int = itemsShown.size
    override fun getItem(position: Int): BoxesContainer.TypeDir = itemsShown[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getFilter(): Filter = object : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemsShown = results!!.values as MutableList<BoxesContainer.TypeDir>
            notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            val filtered = items.filter { constraint!! in it.name }
            filterResults.values = filtered
            return filterResults
        }
    }
}