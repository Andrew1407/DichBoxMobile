package com.diches.dichboxmobile.mv.boxesDataManager

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.ITALIC
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.BoxesContainer

class BoxesListAdapter(
    context: Context,
    private val resource: Int,
    var items: List<BoxesContainer.BoxDataListItem>,
    var itemsShown: List<BoxesContainer.BoxDataListItem>,
) : ArrayAdapter<BoxesContainer.BoxDataListItem>(context, resource, items), Filterable {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater
            .from(context)
            .inflate(resource, parent, false)

        val box = itemsShown[position]
        val viewText = "${box.name} (${box.access_level})"
        val spannable = SpannableString(viewText)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor(box.name_color)),
            0, box.name.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(ITALIC),
            box.name.length + 1, viewText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        spannable.setSpan(
            RelativeSizeSpan(0.9f),
            box.name.length + 1, viewText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        val boxName = view.findViewById<TextView>(R.id.boxesListItem)
        boxName.text = spannable
        return view
    }

    override fun getCount(): Int = itemsShown.size
    override fun getItem(position: Int): BoxesContainer.BoxDataListItem = itemsShown[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getFilter(): Filter = object : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemsShown = results!!.values as MutableList<BoxesContainer.BoxDataListItem>
            notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterParams = constraint!!.split(" ")
            val nameChunk = filterParams[0]
            val boxType = filterParams[1]
            val filterResults = FilterResults()
            val filterByType = { type: String ->
                if (boxType == "all") true
                else type == boxType
            }

            filterResults.values = items.filter {
                nameChunk in it.name && filterByType(it.access_level)
            }

            return filterResults
        }
    }
}